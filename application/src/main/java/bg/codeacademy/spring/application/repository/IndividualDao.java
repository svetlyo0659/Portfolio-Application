package bg.codeacademy.spring.application.repository;

import bg.codeacademy.spring.application.model.Individual;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface IndividualDao
{
  void createIndividual(Individual individual);

  void deleteIndividual(Individual individual);

  List<Individual> getAllIndividuals(String name, String type, String order,Integer pageNum, Integer rowsNum);

  BigDecimal containsIndividual(String name);

  BigDecimal containsIdentifier(String identifier);

  Individual findIndividualByName(String name, String lei);
}

