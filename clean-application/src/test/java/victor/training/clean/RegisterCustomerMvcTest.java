package victor.training.clean;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import victor.training.clean.domain.entity.Customer;
import victor.training.clean.domain.entity.Site;
import victor.training.clean.facade.dto.CustomerDto;
import victor.training.clean.infra.EmailSender;
import victor.training.clean.repo.CustomerRepo;
import victor.training.clean.repo.SiteRepo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("db-mem")
@AutoConfigureMockMvc
@Transactional
public class RegisterCustomerMvcTest {
   private static final ObjectMapper JACKSON = new ObjectMapper();
   @Autowired
   private MockMvc mockMvc;
   @Autowired
   private SiteRepo siteRepo;
   @Autowired
   private CustomerRepo customerRepo;
   @MockBean
   private EmailSender emailSender;
   private Site aSite;
   private CustomerDto requestDto;

   @BeforeEach
   public final void before() {
      aSite = siteRepo.save(new Site());
      requestDto = new CustomerDto()
          .setEmail("::email::")
          .setName("::name::")
          .setSiteId(aSite.getId());
   }
   @Test
   void register_and_get() throws Exception {

      register(requestDto).andExpect(status().isOk());

      assertThat(customerRepo.findAll()).hasSize(1);
      Customer customer = customerRepo.findAll().get(0);
      assertThat(customer.getName()).isEqualTo("::name::");
      assertThat(customer.getEmail()).isEqualTo("::email::");
      assertThat(customer.getSite().getId()).isEqualTo(aSite.getId());
      verify(emailSender).sendEmail(argThat(email -> email.getTo().equals("::email::")));


      CustomerDto responseDto = getCustomer(customer.getId());
      assertThat(responseDto.id).isEqualTo(customer.getId());
      assertThat(responseDto.name).isEqualTo("::name::");
      assertThat(responseDto.email).isEqualTo("::email::");
      assertThat(responseDto.siteId).isEqualTo(aSite.getId());
      assertThat(responseDto.creationDateStr).isEqualTo(now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

   }
   @Test
   void nameTooShortThrows() throws Exception {
      requestDto.name="1";

      register(requestDto).andExpect(status().isInternalServerError());
   }
   @Test
   void existingEmailFails() throws Exception {
      customerRepo.save(new Customer().setEmail("::email::"));

      register(requestDto)
          .andExpect(status().isInternalServerError())
//          .andExpect(status().is4xxClientError())
//          .andExpect(content().string("Customer email is already registered"))
          ;
   }

   private ResultActions register(CustomerDto requestDto) throws Exception {
      return mockMvc.perform(post("/customer")
              .contentType(APPLICATION_JSON)
              .content(JACKSON.writeValueAsString(requestDto))
          );
   }
   private CustomerDto getCustomer(long customerId) throws Exception {
      String responseString = mockMvc.perform(get("/customer/{id}", customerId)
              .accept(APPLICATION_JSON)
      ).andReturn().getResponse().getContentAsString();
      System.out.println("Got response string: " + responseString);
      CustomerDto dto = JACKSON.readValue(responseString, CustomerDto.class);
      return dto;
   }
}
