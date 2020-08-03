package bg.codeacademy.spring.application.controller;

import bg.codeacademy.spring.application.model.Individual;
import bg.codeacademy.spring.application.service.IndividualServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/individual")
public class IndividualController
{

  private final IndividualServiceImpl individualService;

  @Autowired
  private IndividualController(IndividualServiceImpl userService)
  {
    this.individualService = userService;
  }

  @PostMapping
  public ResponseEntity<?> createIndividual(@RequestParam(value = "name") String name,
                                            @RequestParam(value = "type") String type,
                                            @RequestParam(value = "identifier") String identifier)
  {
    BigDecimal check = individualService.containsIndividual(name);
    BigDecimal flag = individualService.containsIdentifier(identifier);

    if (check.equals(BigDecimal.ZERO) && flag.equals(BigDecimal.ZERO) && type.equals("Legal Entity")) {
      individualService.createIndividual(name, type, identifier);
      return ResponseEntity.ok("The Individual was successfully created.");
    }
    else if (flag.equals(BigDecimal.ZERO) && type.equals("Private Entity")) {
      individualService.createIndividual(name, type, identifier);
      return ResponseEntity.ok("The Individual was successfully created.");
    }
    return ResponseEntity.badRequest().body("This individual name or LEI is already taken");
  }

  @GetMapping
  public ResponseEntity<?> getAllIndividuals(@RequestParam(value = "name", required = false,
                                                 defaultValue = "") String name,
                                             @RequestParam(value = "type", required = false,
                                                 defaultValue = "") String type,
                                             @RequestParam(value = "order", required = false,
                                                 defaultValue = " '' ") String order,
                                             @RequestParam(name = "pageNum",
                                                 required = false, defaultValue = "1") Integer pageNum,
                                             @RequestParam(name = "rowsNum",
                                                 required = false, defaultValue = "10") Integer rowsNum)
  {
    List<Individual> all = individualService.getAllIndividuals(name, type, order, pageNum, rowsNum);
    if (!all.isEmpty()) {
      return ResponseEntity.ok(all);
    }
    return ResponseEntity.badRequest().body("No individuals found.");
  }

  @DeleteMapping
  public ResponseEntity<?> deleteIndividual(@RequestParam(value = "identifier") String identifier,
                                            @RequestParam(value = "name") String name)
  {

    BigDecimal flag = individualService.containsIdentifier(identifier);

    if (flag.equals(BigDecimal.ONE)) {
      individualService.deleteIndividual(identifier, name);
      return ResponseEntity.ok("Individual Deleted.");
    }
    return ResponseEntity.badRequest().body("Individual not found.");
  }

}
