package com.boefcity.projectmanagement.repository;

import com.boefcity.projectmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // native SQL query
    // Bruger automatisk prepared statements for at protecte mod SQL injections.
    // Errorhandling: Bruger springs 'DataAccessException' og Transaction management
    /*
    including connection handling, preparing statements,
    setting parameters, executing the query,
    and mapping the results to the User entity.
     */
    @Query(value = "SELECT * FROM app_user WHERE user_id = :userId", nativeQuery = true)
    User findUserByIdNative(@Param("userId") Long userId);

    Optional<User> findUserByUsername(String username);
    /*

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,username,password);
    }

    public User findUserById(Long userId) {
        String query = "SELECT * FROM user WHERE user_id = ?";
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setLong(1, userId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return mapToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by ID", e);
        }
        return null;
    }

    private User mapToUser(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("user_id");
        String username = resultSet.getString("username");
        String email = resultSet.getString("email");
        String password = resultset.getString("password");
        String userRole = resultset.getString("userRole");

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserRole(userRole);

        return user;
    }
     */

}
