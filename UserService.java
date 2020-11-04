package com.neimpetu.starter.service;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neimpetu.starter.controller.UserController;
import com.neimpetu.starter.entity.Parker;
import com.neimpetu.starter.entity.ParkingByUser;
import com.neimpetu.starter.entity.User;
import com.neimpetu.starter.entity.UserInfo;
import com.neimpetu.starter.error.ExceptionHandler;
import com.neimpetu.starter.repository.CompanyDao;
import com.neimpetu.starter.repository.MasterValueDao;
import com.neimpetu.starter.repository.ParkerDao;
import com.neimpetu.starter.repository.ParkingByUserDao;
import com.neimpetu.starter.repository.ProfileDao;
import com.neimpetu.starter.repository.UserDao;
import com.neimpetu.starter.repository.UserInfoDao;
import com.neimpetu.starter.request.AssignParkersAuxRequest;
import com.neimpetu.starter.request.ChangePasswordToRequest;
import com.neimpetu.starter.request.ParkingByUserAuxRequest;
import com.neimpetu.starter.request.UserAuxRequest;
import com.neimpetu.starter.response.GeneralRest;
import com.neimpetu.starter.response.UserDetailResponse;
import com.neimpetu.starter.response.UserInfoSimpleResponse;

@Service
public class UserService {

	private ExceptionHandler exceptionHandler = new ExceptionHandler();

	@Autowired
	UserDao userDao;

	@Autowired
	UserInfoDao userInfoDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger logger = Logger.getLogger(UserController.class);

	/* LIST ALL USERS SUPER */
	/* Listar todos los usuarios administradores siendo el SUPER_USER */
	@PreAuthorize("hasAuthority('SUPER')")
	public ResponseEntity<GeneralRest> tookUsersSuper() {
		try {
			ArrayList<UserInfo> users = userInfoDao
					.findByRoleIdOrderByCreatedDateAsc(masterValueDao.findById(12).get());
			GeneralRest generalRest = new GeneralRest<>(users, "ok", HttpStatus.OK.value());
			return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error listando todos los usuarios como super");
			return new ResponseEntity<GeneralRest>(exceptionHandler.exceptionResponse(e, "USER"),
					HttpStatus.BAD_REQUEST);

		}
	}

	/* CHANGE OWN PASSWORD */
	/* Metodo para cambiar la contraseña del usuario autenticado en el sistema */
	@PreAuthorize("hasAuthority('SUPER') or hasAuthority('ADMIN') or hasAuthority('USER')")
	public ResponseEntity<GeneralRest> changeOwnUserPasswordTo(
			@Valid @RequestBody ChangePasswordToRequest changePasswordAux, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User customJWTUser = (User) authentication.getPrincipal();
			if (passwordEncoder.matches(changePasswordAux.getOldPassword(), customJWTUser.getPassword())) {
				customJWTUser.setPassword(passwordEncoder.encode(changePasswordAux.getNewPassword()));
				this.userDao.save(customJWTUser);
				GeneralRest generalRest = new GeneralRest("La contraseña ha sido actualizada con exito", 200);
				return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);
			} else {
				GeneralRest generalRest = new GeneralRest(
						"Error cambiando la contraseña, la contraseña anterior no coincide con la que se encuentra almacenada en nuestro sistema",
						400);
				return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error cambiando la contraseña del usuario");
			return new ResponseEntity<GeneralRest>(exceptionHandler.exceptionResponse(e, "USER"),
					HttpStatus.BAD_REQUEST);
		}
	}

	/* CHANGE PASSWORD FOR USER ID */
	/* Metodo para cambiar la contraseña de un usuario del sistema */
	@PreAuthorize("hasAuthority('SUPER') or hasAuthority('ADMIN')")
	public ResponseEntity<GeneralRest> changeUserPasswordTo(
			@Valid @RequestBody ChangePasswordToRequest changePasswordAux, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			User userToChangePassword = this.userDao
					.findById(this.userInfoDao.findById(changePasswordAux.getUserId()).get().getUserId().getId()).get();
			userToChangePassword.setPassword(passwordEncoder.encode(changePasswordAux.getNewPassword()));
			this.userDao.save(userToChangePassword);
			GeneralRest generalRest = new GeneralRest("La contraseña ha sido actualizada con exito", 200);
			return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error cambiando la contraseña del usuario");
			return new ResponseEntity<GeneralRest>(exceptionHandler.exceptionResponse(e, "USER"),
					HttpStatus.BAD_REQUEST);

		}
	}
	
	/* LIST ALL USERS ADMIN */
	/* Listar todos los usuarios siendo el ADMIN_USER */
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<GeneralRest> tookUsersAdmin() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User customJWTUser = (User) authentication.getPrincipal();
			UserInfo userInfo = userInfoDao.findByUserId(this.userDao.findById(customJWTUser.getId()).get());
			ArrayList<UserInfo> users = userInfoDao
					.findByCompanyIdAndUserIdNotOrderByCreatedDateAsc(userInfo.getCompanyId(), customJWTUser);
			GeneralRest generalRest = new GeneralRest<>(users, "ok", HttpStatus.OK.value());
			return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error listando todos los usuarios como administrador");
			return new ResponseEntity<GeneralRest>(exceptionHandler.exceptionResponse(e, "USER"),
					HttpStatus.BAD_REQUEST);
		}
	}


	/* LIST ALL USERS */
	/* Listar todos los usuarios teniendo un rol diferente a SUPER o ADMIN */
	@PreAuthorize("!hasAuthority('ADMIN') or !hasAuthority('SUPER')")
	public ResponseEntity<GeneralRest> tookUsers() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User customJWTUser = (User) authentication.getPrincipal();
			UserInfo userInfo = userInfoDao.findByUserId(this.userDao.findById(customJWTUser.getId()).get());
			ArrayList<UserInfo> users = userInfoDao
					.findByCompanyIdAndUserIdNotAndRoleIdNotAndRoleIdNotOrderByCreatedDateAsc(userInfo.getCompanyId(),
							customJWTUser, masterValueDao.findById(11).get(), masterValueDao.findById(12).get());
			GeneralRest generalRest = new GeneralRest<>(users, "ok", HttpStatus.OK.value());
			return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error listando todos los usuarios como usuario");
			return new ResponseEntity<GeneralRest>(exceptionHandler.exceptionResponse(e, "USER"),
					HttpStatus.BAD_REQUEST);
		}
	}

	/* CREATE USER FOR SUPER */
	/* Metodo para crear usuarios en el sistema siendo un super */
	@PreAuthorize("hasAuthority('SUPER')")
	public ResponseEntity<GeneralRest> createUserForSuper(@Valid @RequestBody UserAuxRequest newUserAux,
			HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User customJWTUser = (User) authentication.getPrincipal();
		User newUser = newUserAux.getUser();
		UserInfo newUserInfo = newUserAux.getUserInfo();
		try {
			if (!ifUserNameExists(newUser.getUsername())) {
				newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
				User savedUser = userDao.save(newUser);
				newUserInfo.setUserId(savedUser);
				newUserInfo.setCreatorId(customJWTUser);
				newUserInfo.setCreatedDate(new Date());
				newUserInfo.setActivatedProfileId(
						profileDao.findById(newUserAux.getUserInfo().getActivatedProfileId().getId()).get());
				newUserInfo.setCompanyId(companyDao.findById(newUserAux.getUserInfo().getCompanyId().getId()).get());
				UserInfo savedUserInfo = this.userInfoDao.save(newUserInfo);
				this.userDao.saveAuthority(newUser.getId(), 2);
				GeneralRest generalRest = new GeneralRest(newUserInfo, "creación del usuario exitosa", 201);
				return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);
			} else {
				GeneralRest generalRest = new GeneralRest(
						"error creando el usuario, " + "ya existe un usuario con el mismo correo", 400);
				return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("Error en el listar de planes");
			return new ResponseEntity<GeneralRest>(exceptionHandler.exceptionResponse(e, "USER"),
					HttpStatus.BAD_REQUEST);
		}
	}

	/* CREATE USER FOR NO SUPER */
	/* Metodo para crear usuarios en el sistema siendo no super */
	@PreAuthorize("!hasAuthority('SUPER')")
	public ResponseEntity<GeneralRest> createUserForNoSuper(@Valid @RequestBody UserAuxRequest newUserAux,
			HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User customJWTUser = (User) authentication.getPrincipal();
		UserInfo userInfoAuthenticated = userInfoDao.findByUserId(this.userDao.findById(customJWTUser.getId()).get());
		User newUser = newUserAux.getUser();
		UserInfo newUserInfo = newUserAux.getUserInfo();
		try {
			if (!ifUserNameExists(newUser.getUsername())) {
				newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
				User savedUser = userDao.save(newUser);
				newUserInfo.setUserId(savedUser);
				newUserInfo.setCreatorId(customJWTUser);
				newUserInfo.setCreatedDate(new Date());
				newUserInfo.setActivatedProfileId(
						profileDao.findById(newUserAux.getUserInfo().getActivatedProfileId().getId()).get());
				newUserInfo.setCompanyId(userInfoAuthenticated.getCompanyId());
				newUserInfo.setActivatedPlanId(userInfoAuthenticated.getActivatedPlanId());
				UserInfo savedUserInfo = this.userInfoDao.save(newUserInfo);
				if (savedUserInfo.getRoleId().getId() == 14) {
					this.userDao.saveAuthority(newUser.getId(), 2);
				} else {
					this.userDao.saveAuthority(newUser.getId(), 3);
				}
				GeneralRest generalRest = new GeneralRest(newUserInfo, "creación del usuario exitosa", 201);
				return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);
			} else {
				GeneralRest generalRest = new GeneralRest(
						"error creando el usuario, " + "ya existe un usuario con el mismo correo", 400);
				return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("Error en el listar de planes");
			return new ResponseEntity<GeneralRest>(exceptionHandler.exceptionResponse(e, "USER"),
					HttpStatus.BAD_REQUEST);
		}
	}

	/* Metodo que verifica si exista un correo ya en el sistema */
	public boolean ifEmailExists(String email) {
		if (this.userInfoDao.findByEmail(email) != null) {
			return true;
		} else {
			return false;
		}
	}

	// metodo que recibe un userName y responde con un booblean si el userName
	// ya esta almacenado en la BD
	public boolean ifUserNameExists(String userName) {
		if (this.userDao.findByUsername(userName) != null) {
			return true;
		} else {
			return false;
		}
	}

	/* UPDATE USER */
	/* Metodo para actualizar usuarios en el sistema */
	@PreAuthorize("hasAuthority('SUPER') or hasAuthority('ADMIN') or hasAuthority ('USER')")
	public @ResponseBody ResponseEntity<GeneralRest> updateUser(@RequestBody UserInfo userToUpdate,
			HttpServletRequest request, HttpServletResponse response) {
		UserInfo toUpdate = this.userInfoDao.findById(userToUpdate.getId()).get();
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User customJWTUser = (User) authentication.getPrincipal();
			if (!(userToUpdate.getEmail().equals(toUpdate.getEmail()))) {
				if (this.ifEmailExists(userToUpdate.getEmail())) {
					GeneralRest generalRest = new GeneralRest(
							"error creando el usuario, " + "ya exis	te un usuario con el mismo username", 400);
					return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);
				} else {
					userToUpdate.setLastUpdateDate(new Date());
					userToUpdate.setLastUpdateId(customJWTUser);
					this.userInfoDao.save(userToUpdate);
					GeneralRest generalRest = new GeneralRest("Exito actualizando el usuario", 200);
					return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);
				}
			}
			userToUpdate.setLastUpdateDate(new Date());
			userToUpdate.setLastUpdateId(customJWTUser);
			this.userInfoDao.save(userToUpdate);
			GeneralRest generalRest = new GeneralRest("Exito actualizando el usuario", 200);
			return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error actualizando usuario");
			return new ResponseEntity<GeneralRest>(exceptionHandler.exceptionResponse(e, "USER"),
					HttpStatus.BAD_REQUEST);
		}
	}

	/* DELETE USER */
	/* SE RECIBE UN correo de un USUARIO PARA EL BORRADO DEL MISMO */
	@PreAuthorize("hasAuthority('SUPER') or hasAuthority('ADMIN') or hasAuthority ('USER')")
	public @ResponseBody ResponseEntity<GeneralRest> deleteUser(@RequestParam String username) {
		User userAux = this.userDao.findByUsername(username);
		UserInfo userInfoAux = this.userInfoDao.findByEmail(username);
		try {
			this.userInfoDao.delete(userInfoAux);
			this.userDao.delete(userAux);
			GeneralRest generalRest = new GeneralRest("Borrado del usuario exitoso", 200);
			return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);
		} catch (Exception e) {
			Throwable t = e.getCause();
			if (t instanceof ConstraintViolationException) {
				e.printStackTrace();
				logger.error("El usuario se encuentra asociado en el sistema");
				return new ResponseEntity<GeneralRest>(exceptionHandler.exceptionResponse(e, "USER"),
						HttpStatus.BAD_REQUEST);
			} else {
				e.printStackTrace();
				logger.error("Error en el borrado del usuario");
				return new ResponseEntity<GeneralRest>(exceptionHandler.exceptionResponse(e, "USER"),
						HttpStatus.BAD_REQUEST);
			}
		}
	}

	/* List not super users id, name to select */
	// listar usuarios del sistema para seleccionador
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority ('USER')")
	public ResponseEntity<GeneralRest> tookNameIdUsers(HttpServletRequest request, HttpServletResponse response,
			@Valid @RequestParam Integer roleId) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User customJWTUser = (User) authentication.getPrincipal();
			UserInfo userInfoAuthenticated = userInfoDao
					.findByUserId(this.userDao.findById(customJWTUser.getId()).get());
			ArrayList<UserInfo> users = userInfoDao.getSimpleInfoUser(userInfoAuthenticated.getCompanyId().getId(),
					roleId);
			ArrayList<UserInfoSimpleResponse> userList = new ArrayList<>();
			for (UserInfo userInfo : users) {
				UserInfoSimpleResponse userAux = new UserInfoSimpleResponse();
				userAux.setCompleteName(userInfo.getCompleteName());
				userAux.setId(userInfo.getId());
				userList.add(userAux);
			}
			GeneralRest generalRest = new GeneralRest(userList, "listado de usuarios exitoso", 200);

			return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);
		} catch (Exception ex) {
			this.logger.error("Error mapeando los usuarios ");
			ex.printStackTrace();
			GeneralRest generalRest = new GeneralRest("listado de usuarios fallido", 400);
			return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.BAD_REQUEST);
		}
	}

	/* Get info user from an user */
	/*
	 * Método para obtener la información relacionada a un usuario en el sistema que
	 * se encuentra autenticado y realiza esta petición
	 */
	@PreAuthorize("hasAuthority('SUPER') or hasAuthority('ADMIN') or hasAuthority ('USER')")
	public ResponseEntity<GeneralRest> getAuthenticatedUserInfo() {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User customJWTUser = (User) authentication.getPrincipal();
			UserInfo userInfo = userInfoDao.findByUserId(this.userDao.findById(customJWTUser.getId()).get());
			GeneralRest generalRest = new GeneralRest(userInfo, "Exito", 200);
			return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.OK);
		} catch (Exception e) {
			this.logger.error("Error obteniendo info del usuario ", e);
			GeneralRest generalRest = new GeneralRest("Error", 400);
			return new ResponseEntity<GeneralRest>(generalRest, HttpStatus.BAD_REQUEST);

		}

	}




	// Adicionado para retornar instancia usuario
	public User getNewUser() {
		return new User();
	}

	// Adicionado para retornar instancia infoUser
	public UserInfo getNewInfoUser() {
		return new UserInfo();
	}

	public UserDetailResponse getNewRestUserDetail() {
		return new UserDetailResponse();
	}
}
