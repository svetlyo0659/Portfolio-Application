package bg.codeacademy.spring.application.service;


import bg.codeacademy.spring.application.model.Individual;

import java.math.BigDecimal;
import java.util.List;

public interface IndividualService
{
  List<Individual> getAllIndividuals(String name, String type, String order,
                                     Integer pageNum, Integer rowsNum);

  void createIndividual(String name, String type, String identifier);

  void deleteIndividual(String identifier, String name);

  BigDecimal containsIndividual(String name);

  BigDecimal containsIdentifier(String identifier);

  Individual findIndividualByName(String name, String lei);
}
