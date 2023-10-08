package com.inn.cafe.serviceImple;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.POJO.Product;
import com.inn.cafe.constants.CafeConstant;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.service.ProductService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class ProductServiceImple implements ProductService {
    @Autowired
    ProductDao productDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {

        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap,false)){
                    productDao.save(getProductFromMap(requestMap,false));
                    return CafeUtils.getResponseEntity("Product Added Successfully!",HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity(CafeConstant.INVALID_ID_DATA,HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try{
           return new ResponseEntity<>(productDao.getAllProduct(),HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> updateProsuct(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductMap(requestMap,true)){
                   Optional<Product> optionalProduct=productDao.findById(Integer.parseInt(requestMap.get("id")));
                   if(!optionalProduct.isEmpty()){
                       Product product = getProductFromMap(requestMap,true);
                       product.setStatus(optionalProduct.get().getStatus());
                       productDao.save(product);
                       return CafeUtils.getResponseEntity("Product Updated Successfully",HttpStatus.OK);
                   }
                   return CafeUtils.getResponseEntity("Product Not Found",HttpStatus.NOT_FOUND);
                }
                return CafeUtils.getResponseEntity(CafeConstant.INVALID_ID_DATA,HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<Product> optionalProduct  = productDao.findById(id);
                if(!optionalProduct.isEmpty()){
                    productDao.deleteById(id);
                    return CafeUtils.getResponseEntity("Product Deleted SuccessFully",HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Product Not Found",HttpStatus.NOT_FOUND);
            }
            return CafeUtils.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProductStatus(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional<Product> optionalProduct  = productDao.findById(Integer.parseInt(requestMap.get("id")));
                if(!optionalProduct.isEmpty()){
                    Integer ressult=productDao.updateProductStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity("Product Status Updated.",HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Product Not Found",HttpStatus.NOT_FOUND);
            }
            return CafeUtils.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {

        Category category=new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        Product product = new Product();
        if(isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }else{
            product.setStatus("true");
        }

        product.setName(requestMap.get("name"));
        product.setCategory(category);
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;


    }

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("id") && validateId){
            return true;
        }else if(!validateId){
            return true;
        }
        return false;

    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProductByCategory(Integer id) {
        try{
            return new ResponseEntity<>(productDao.getAllProductByCategory(id),HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try{
            return new ResponseEntity<>(productDao.getProductById(id),HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<ProductWrapper> (new ProductWrapper(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
