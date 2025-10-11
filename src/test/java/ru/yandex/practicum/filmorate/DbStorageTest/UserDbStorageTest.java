package ru.yandex.practicum.filmorate.DbStorageTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles("test")
@Import(UserDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {

        jdbcTemplate.execute("DELETE FROM friends");
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM films_genres");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    @DisplayName("Should return user when find by existing id")
    void should_Return_User_When_Find_By_Existing_Id() {

        User user = createTestUser("test@mail.ru", "testLogin", "Test Name");
        User savedUser = userStorage.createUser(user);

        Optional<User> foundUser = userStorage.getUser(savedUser.getId());

        assertThat(foundUser)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("id", savedUser.getId())
                .hasFieldOrPropertyWithValue("email", "test@mail.ru")
                .hasFieldOrPropertyWithValue("login", "testLogin")
                .hasFieldOrPropertyWithValue("name", "Test Name")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("Should return empty when find by non-existing id")
    void should_Return_Empty_When_Find_By_Non_Existing_Id() {

        Optional<User> foundUser = userStorage.getUser(999L);

        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("Should create user with generated id")
    void should_Create_User_With_Generated_Id() {

        User user = createTestUser("create@mail.ru", "createLogin", "Create Name");

        User savedUser = userStorage.createUser(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull().isPositive();
        assertThat(savedUser.getEmail()).isEqualTo("create@mail.ru");
        assertThat(savedUser.getLogin()).isEqualTo("createLogin");
        assertThat(savedUser.getName()).isEqualTo("Create Name");
        assertThat(savedUser.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("Should update user when user exists")
    void should_Update_User_When_User_Exists() {

        User user = createTestUser("update@mail.ru", "updateLogin", "Update Name");
        User savedUser = userStorage.createUser(user);

        savedUser.setName("Updated Name");
        savedUser.setLogin("updatedLogin");
        savedUser.setEmail("updated@mail.ru");
        User updatedUser = userStorage.updateUser(savedUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getLogin()).isEqualTo("updatedLogin");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@mail.ru");
    }

    @Test
    @DisplayName("Should return all users")
    void should_Return_All_Users() {

        User user1 = createTestUser("user1@mail.ru", "user1", "User One");
        User user2 = createTestUser("user2@mail.ru", "user2", "User Two");
        userStorage.createUser(user1);
        userStorage.createUser(user2);

        List<User> users = userStorage.getUsers();

        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("user1@mail.ru", "user2@mail.ru");
    }

    @Test
    @DisplayName("Should add friend when users exist")
    void should_Add_Friend_When_Users_Exist() {

        User user1 = createTestUser("user1@mail.ru", "user1", "User One");
        User user2 = createTestUser("user2@mail.ru", "user2", "User Two");
        User savedUser1 = userStorage.createUser(user1);
        User savedUser2 = userStorage.createUser(user2);

        userStorage.addFriend(savedUser1.getId(), savedUser2.getId());
        List<User> friends = userStorage.getFriends(savedUser1.getId());

        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(savedUser2.getId());
        assertThat(friends.get(0).getEmail()).isEqualTo("user2@mail.ru");
    }

    @Test
    @DisplayName("Should remove friend when friendship exists")
    void should_Remove_Friend_When_Friendship_Exists() {

        User user1 = createTestUser("user1@mail.ru", "user1", "User One");
        User user2 = createTestUser("user2@mail.ru", "user2", "User Two");
        User savedUser1 = userStorage.createUser(user1);
        User savedUser2 = userStorage.createUser(user2);
        userStorage.addFriend(savedUser1.getId(), savedUser2.getId());

        userStorage.removeFriend(savedUser1.getId(), savedUser2.getId());
        List<User> friends = userStorage.getFriends(savedUser1.getId());

        assertThat(friends).isEmpty();
    }

    @Test
    @DisplayName("Should return common friends when users have common friends")
    void should_Return_Common_Friends_When_Users_Have_Common_Friends() {

        User user1 = createTestUser("user1@mail.ru", "user1", "User One");
        User user2 = createTestUser("user2@mail.ru", "user2", "User Two");
        User commonUser = createTestUser("common@mail.ru", "common", "Common User");

        User savedUser1 = userStorage.createUser(user1);
        User savedUser2 = userStorage.createUser(user2);
        User savedCommon = userStorage.createUser(commonUser);

        userStorage.addFriend(savedUser1.getId(), savedCommon.getId());
        userStorage.addFriend(savedUser2.getId(), savedCommon.getId());

        List<User> commonFriends = userStorage.getCommonFriends(savedUser1.getId(), savedUser2.getId());

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(savedCommon.getId());
        assertThat(commonFriends.get(0).getEmail()).isEqualTo("common@mail.ru");
    }

    @Test
    @DisplayName("Should load friends for user")
    void should_Load_Friends_For_User() {

        User user1 = createTestUser("user1@mail.ru", "user1", "User One");
        User user2 = createTestUser("user2@mail.ru", "user2", "User Two");
        User savedUser1 = userStorage.createUser(user1);
        User savedUser2 = userStorage.createUser(user2);
        userStorage.addFriend(savedUser1.getId(), savedUser2.getId());

        Optional<User> foundUser = userStorage.getUser(savedUser1.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getFriends()).contains(savedUser2.getId());
    }

    @Test
    @DisplayName("Should return empty list when user has no friends")
    void should_Return_Empty_Friends_List_When_User_Has_No_Friends() {

        User user = createTestUser("user@mail.ru", "user", "User");
        User savedUser = userStorage.createUser(user);

        List<User> friends = userStorage.getFriends(savedUser.getId());

        assertThat(friends).isEmpty();
    }

    private User createTestUser(String email, String login, String name) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }
}
