package com.neimpetu.starter.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.neimpetu.starter.constant.ApiConstant;
import com.neimpetu.starter.entity.UserInfo;
import com.neimpetu.starter.request.AssignParkersAuxRequest;
import com.neimpetu.starter.request.ChangePasswordToRequest;
import com.neimpetu.starter.request.ParkingByUserAuxRequest;
import com.neimpetu.starter.request.UserAuxRequest;
import com.neimpetu.starter.response.GeneralRest;
import com.neimpetu.starter.service.UserService;

@RestController
@RequestMapping(ApiConstant.USER_CONTROLLER_API)
public class UserController {

	@Autowired
	private UserService userService;

	/* ACTUALIZAR USER */
	/* Metodo para actualizar usuarios en el sistema */
	@PostMapping(value = ApiConstant.USER_CONTROLLER_API_UPDATE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GeneralRest> update(@Valid @RequestBody UserInfo user, HttpServletRequest request,
			HttpServletResponse response) {
		return userService.updateUser(user, request, response);
	}

	/* CREATE USER FOR SUPER/
	/* Metodo para crear usuarios en el sistema siendo un super*/
	@PostMapping(value = ApiConstant.USER_CONTROLLER_API_SUPER_CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GeneralRest> createUser(@Valid @RequestBody UserAuxRequest newUserAux, HttpServletRequest request,
			HttpServletResponse response) {
		return this.userService.createUserForSuper(newUserAux, request, response);
	}
	
	/* CREATE USER FOR NO SUPER*/
	/* Metodo para crear usuarios en el sistema siendo no super*/
	@PostMapping(value = ApiConstant.USER_CONTROLLER_API_CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GeneralRest> createUserAdmin(@Valid @RequestBody UserAuxRequest newUserAux, HttpServletRequest request,
			HttpServletResponse response) {
		return this.userService.createUserForNoSuper(newUserAux, request, response);
	}
	
	/* LIST ALL USERS FOR ADMIN */
	/* Listar todos los usuarios siendo el ADMIN_USER */
	@GetMapping(value = ApiConstant.USER_CONTROLLER_API_ADMIN_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GeneralRest> tookUsersAdmin() {
		return userService.tookUsersAdmin();
	}

	
	/* CHANGE PASSWORD FOR USER ID*/
	/* Metodo para cambiar la contraseña de un usuario del sistema*/
	@PostMapping(value = ApiConstant.USER_CONTROLLER_API_CHANGE_PASSWORD, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GeneralRest> changeUserPassword(@Valid @RequestBody ChangePasswordToRequest changePasswordAux, HttpServletRequest request,
			HttpServletResponse response) {
		return this.userService.changeUserPasswordTo(changePasswordAux, request, response);
	}
	
	/* CHANGE PASSWORD FOR USER ID*/
	/* Metodo para cambiar la contraseña de un usuario del sistema*/
	@PostMapping(value = ApiConstant.USER_CONTROLLER_API_CHANGE_OWN_PASSWORD, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GeneralRest> changeOwnUserPassword(@Valid @RequestBody ChangePasswordToRequest changePasswordAux, HttpServletRequest request,
			HttpServletResponse response) {
		return this.userService.changeOwnUserPasswordTo(changePasswordAux, request, response);
	}

	/* DELETE USER */
	/* Metodo para eliminar usuarios por correo en el sistema */
	@DeleteMapping(value = ApiConstant.USER_CONTROLLER_API_DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<GeneralRest> deleteUser(@RequestParam String username) {
		return userService.deleteUser(username);
	}

	/* LIST ALL USERS */
	/* Listar todos los usuarios siendo el SUPER_USER */
	@GetMapping(value = ApiConstant.USER_CONTROLLER_API_SUPER_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GeneralRest> tookUsersSuper() {
		return userService.tookUsersSuper();
	}
	
	/* LIST ALL USERS FOR NOT ADMIN NOT SUPER */
	/* Listar todos los usuarios teniendo un rol distinto a super y admin */
	@GetMapping(value = ApiConstant.USER_CONTROLLER_API_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GeneralRest> tookUsers() {
		return userService.tookUsers();
	}

	/* List not super users id, name to select */
	// listar usuarios del sistema para seleccionador
	@GetMapping(value = ApiConstant.USER_CONTROLLER_API_ID_NAME_LIST_ROLE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<GeneralRest> tookNameIdUsers(
			HttpServletRequest request, HttpServletResponse response, @Valid @RequestParam Integer roleId) {
		return userService.tookNameIdUsers(request, response, roleId);
	}

	/* Get info user from an user */
	/* Método para obtener la información relacionada a un usuario en el sistema que se encuentra autenticado y realiza esta petición */
	@GetMapping(value = ApiConstant.USER_CONTOLLER_GET_INFO_USER, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<GeneralRest>  getAuthenticatedUserInfo() {
		return userService.getAuthenticatedUserInfo();
	}
	

	

	
}