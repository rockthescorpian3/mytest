package com.mkt.mytest.admin.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mkt.mytest.admin.persist.entity.Category;
import com.mkt.mytest.admin.service.CategoryService;
import com.mkt.mytest.base.exception.AppCustomException;
import com.mkt.mytest.base.json.AppError;
import com.mkt.mytest.base.json.CommonJson;
import com.mkt.mytest.base.json.ErrorMessage;
import com.mkt.mytest.base.json.GetEntityResponse;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	CategoryService categoryService;
	
	@ResponseStatus(value = HttpStatus.CREATED)
	@RequestMapping(value={"/categorys"},method=RequestMethod.POST)
	GetEntityResponse saveCategory(@Valid @RequestBody Category category){
		long id=categoryService.saveCategory(category);
		return	new GetEntityResponse(null, "http://localhost:8080/mytest/admin/categorys",
				null, new CommonJson(0,"Category is save with Id:"+id,null));
	}
	
	@RequestMapping(value="/categorys",method=RequestMethod.GET)
	GetEntityResponse getAllCategory(){
		List<Category> categorys=categoryService.getAllCategory();
		return new GetEntityResponse(categorys,"Get:mytest/admin/categorys", null, null);

	}
	
	@RequestMapping(value="/categorys/{id}",method=RequestMethod.GET)
	GetEntityResponse getCategoryById(@PathVariable long id){
		List<Category> responseList=new ArrayList<Category>();
		responseList.add(categoryService.getCategoryById(id));
		return new GetEntityResponse(responseList,"Get:mytest/admin/categorys/"+id,null, null);
	}
	
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	@RequestMapping(value={"/categorys"},method=RequestMethod.PUT)
	void updatesCategory(@Valid @RequestBody Category category){
		
			if(categoryService.getCategoryById(category.getPkId()) == null){
				String className=category.getClass().getSimpleName();
				AppCustomException appExp=new AppCustomException();
				appExp.setAction("Update the "+className);
				appExp.setMessage("Can not update the "+className+" ");
				appExp.setSuggestion("this "+className+" can not found in the database. Provide vaild one");
				throw appExp;
			}
			categoryService.updateCategory(category);	
	}
	
	
	@RequestMapping(value={"/categorys/{id}"},method=RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	void deleteCategory(@PathVariable long id){		
		if(categoryService.getCategoryById(id) == null){
			AppCustomException appExp=new AppCustomException();
			appExp.setAction("Delete the Category");
			appExp.setMessage("Can not delete the category ");
			appExp.setSuggestion("this is not found in the database. Provide vaild one");
			throw appExp;
		}		
		categoryService.deleteCategory(id);	
	}
	
	
	@ExceptionHandler(AppCustomException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	ErrorMessage AppCustomExceptionHandler(AppCustomException appExp){
		ErrorMessage errorMessage=new ErrorMessage(appExp.getMessage());
		errorMessage.addError(new AppError(appExp.getAction(), appExp.getMessage(), appExp.getSuggestion()));
		return errorMessage;
	}
}
