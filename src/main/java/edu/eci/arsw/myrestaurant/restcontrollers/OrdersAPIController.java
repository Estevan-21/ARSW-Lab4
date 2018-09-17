/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.myrestaurant.restcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.ProductType;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServices;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hcadavid
 */

@RestController
@RequestMapping(value = "/orders")
@Service
public class OrdersAPIController {        
        @Autowired
        RestaurantOrderServices restaurant;
        
        @RequestMapping(value = "/{idmesa}",method = RequestMethod.GET)         
 	public ResponseEntity<?> manejadorGetRecursoXX(@PathVariable int idmesa) {
            if(restaurant.getTablesWithOrders().contains(idmesa)){         
            Order data = restaurant.getTableOrder(idmesa);
            return new ResponseEntity<>(data,HttpStatus.ACCEPTED); 
            }  else{ 		
 		return new ResponseEntity<>("ERROR 404 \n La orden no existe",HttpStatus.NOT_FOUND);
            }  
 	}      
        
        
        @RequestMapping(method = RequestMethod.GET)          
 	public ResponseEntity<?> manejadorGetRecursoXX() throws OrdersAPIControllerException{
            Map<Integer, Order> data = restaurant.getOrders();             
            return new ResponseEntity<>(data,HttpStatus.ACCEPTED);  
 	}   
        
        @RequestMapping(method = RequestMethod.POST)	
	public ResponseEntity<?> manejadorPostRecursoXX(@RequestBody Order o){
		try {                                              
			restaurant.addNewOrderToTable(o);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception ex) {
			Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
			return new ResponseEntity<>("Error, no se ha podido agregar la orden",HttpStatus.FORBIDDEN);            
		}        
	
	}
        
        @RequestMapping(value = "/{idmesa}/total",method = RequestMethod.GET)
        public ResponseEntity<?> manejadorGetRecursoTotal(@PathVariable int idmesa) throws OrderServicesException {
            if(restaurant.getTablesWithOrders().contains(idmesa)){         
            int data = restaurant.calculateTableBill(idmesa);
            return new ResponseEntity<>("El valor total de la orden número "+idmesa+" es: "+data,HttpStatus.ACCEPTED);  
        }else{ 		
 		return new ResponseEntity<>("ERROR 404 \n La orden no existe",HttpStatus.NOT_FOUND);
            }  
        }
        
        @RequestMapping(value = "/{idmesa}/add",method = RequestMethod.GET)
        public ResponseEntity<?> manejadorPutNuevaOrden(@PathVariable int idmesa,@RequestBody String p) throws OrderServicesException {
            try {                                              
			Order or = restaurant.getTableOrder(idmesa);
                        or.addDish(p, 1);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (Exception ex) {
			Logger.getLogger(OrdersAPIController.class.getName()).log(Level.SEVERE, null, ex);
			return new ResponseEntity<>("Error, no se ha podido agregar el producto",HttpStatus.FORBIDDEN);            
		}   
        }
        private static final String PATH = "/error";

        @RequestMapping(value = PATH)
        public String error() {
            return "Error handling";
        }

        public String getErrorPath() {
            return PATH;
        }

}
