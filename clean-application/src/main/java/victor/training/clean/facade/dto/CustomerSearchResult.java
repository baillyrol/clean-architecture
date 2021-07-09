package victor.training.clean.facade.dto;


// stinks of JSON
public class CustomerSearchResult {
   private final long id;
   private final String name;

   public CustomerSearchResult(long id, String name) {
      this.id = id;
      this.name = name;
   }

   public long getId() {
      return id;
   }

   public String getName() {
      return name;
   }
}
