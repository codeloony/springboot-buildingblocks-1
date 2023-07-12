package com.stacksimplify.restservices.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.stacksimplify.restservices.entities.Order;
import com.stacksimplify.restservices.entities.User;
import com.stacksimplify.restservices.exceptions.UserNotFoundException;
import com.stacksimplify.restservices.repositories.UserRepository;
import com.stacksimplify.restservices.services.UserService;
@RestController
@RequestMapping(value = "/hateoas/users")
@Validated
public class UserHateoasController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	// getUserById
	@GetMapping("/{id}")
	public EntityModel<User> getUserById(@PathVariable("id") @Min(1) Long id) {
		 EntityModel<User> model = null;
		try {
			Optional<User> userOptional =  userService.getUserById(id);
			User user = userOptional.get();
			Long userid = user.getUserid();
			Link selflink =  WebMvcLinkBuilder.linkTo(this.getClass()).slash("getUserById").slash(id).withRel("get-user");
			user.add(selflink);
			//Resource<User> finalResource = new Resource<User>(user);
			return model;
			
		} catch (UserNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}

	}
	
	// getAllUsers Method
	@GetMapping
	public CollectionModel<User> getAllUsers() throws UserNotFoundException {
		List<User> allusers = userService.getAllUsers();
		
		for(User user : allusers) {
			//Self Link 
			Long userid = user.getUserid();
			Link selflink = WebMvcLinkBuilder.linkTo(this.getClass()).slash(userid).withSelfRel();
			user.add(selflink);
			
			//Relationship link with getAllOrders
			CollectionModel<Order> orders = WebMvcLinkBuilder.methodOn(OrderHateoasController.class)
					.getAllOrders(userid);
			Link orderslink = WebMvcLinkBuilder.linkTo(orders).withRel("all-orders");
			user.add(orderslink);
			
		}
		//Self link for getAllUsers
		Link selflinkgetAllUsers = WebMvcLinkBuilder.linkTo(this.getClass()).withSelfRel();
		CollectionModel<User> finalResources = CollectionModel.of(allusers, selflinkgetAllUsers);
				
		return finalResources;

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
