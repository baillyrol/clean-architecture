package victor.training.clean.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import victor.training.clean.facade.CustomerFacade;
import victor.training.clean.facade.dto.CustomerDto;
import victor.training.clean.facade.dto.CustomerSearchCriteria;
import victor.training.clean.facade.dto.CustomerSearchResult;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CustomerController {
   @Autowired
   private CustomerFacade customerFacade;

   @GetMapping("{id}")
   public CustomerDto findById(@PathVariable long id) {
      return customerFacade.findById(id);
   }

   @PostMapping("search")
   public List<CustomerSearchResult> search(@RequestBody CustomerSearchCriteria searchCriteria) {
      return customerFacade.search(searchCriteria);
   }

   @PostMapping
   //   @PreAuthorized("hasRole('CRM')")
   public void register(@Valid  @RequestBody CustomerDto customerDto) {
      customerFacade.register(customerDto);
   }
}
