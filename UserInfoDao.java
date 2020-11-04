package com.neimpetu.starter.repository;

import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.neimpetu.starter.entity.Company;
import com.neimpetu.starter.entity.MasterValue;
import com.neimpetu.starter.entity.User;
import com.neimpetu.starter.entity.UserInfo;

@Transactional
public interface UserInfoDao extends CrudRepository<UserInfo, Integer> {

	UserInfo findByUserId(User id);

	UserInfo findByEmail(String email);

	@Query(value = "SELECT complete_name FROM infouser where id = :creatorId", nativeQuery = true)
	public String findNameCreator(@Param("creatorId") Integer creatorId);

	@Query(value = "SELECT name FROM plan where id = :planId", nativeQuery = true)
	public String findNamePlan(@Param("planId") Integer planId);
	
	@Query(value = "SELECT name FROM profile where id = :profileId", nativeQuery = true)
	public String findNameProfile(@Param("profileId") Integer profileId);
	
	@Query(value = "select * from infouser where role_id = :roleId and company_id = :companyId", nativeQuery = true)
	ArrayList<UserInfo> getSimpleInfoUser(@Param("companyId") Integer companyId, @Param("roleId") Integer roleId);
	
	ArrayList<UserInfo> findByRoleIdOrderByCreatedDateAsc(MasterValue masterValue);
	
	ArrayList<UserInfo> findByCompanyIdAndUserIdNotAndRoleIdNotAndRoleIdNotOrderByCreatedDateAsc(Company companyId, User userNot, MasterValue roleSuper, MasterValue roleAdmin);

	ArrayList<UserInfo> findByCompanyIdAndUserIdNotOrderByCreatedDateAsc(Company companyId, User userNot);
}
