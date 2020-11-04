package com.neimpetu.starter.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.neimpetu.starter.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

	@Query("SELECT DISTINCT user FROM User user " + "INNER JOIN FETCH user.authorities AS authorities "
			+ "WHERE user.username = :username")
	User findByUsername(@Param("username") String username);

	@Query(value = "INSERT into users_authorities (users_id, authority_id) " + "values (:userId, :authorityId) "
			+ "returning 1", nativeQuery = true)
	int saveAuthority(@Param("userId") Integer userId, @Param("authorityId") Integer authorityId);

}
