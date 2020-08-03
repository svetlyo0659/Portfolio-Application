package bg.codeacademy.spring.application.service;

import bg.codeacademy.spring.application.model.Individual;
import bg.codeacademy.spring.application.repository.IndividualRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class IndividualServiceImpl implements IndividualService
{
  @Autowired
  private IndividualRepository individualRepository;


  @Override
  public List<Individual> getAllIndividuals(String name, String type, String order,
                                            Integer pageNum, Integer rowsNum)
  {
    return individualRepository.getAllIndividuals(name, type, order,pageNum,rowsNum);
  }

  @Override
  public void createIndividual(String name, String type, String identifier)
  {
    Individual individual = new Individual()
        .setName(name)
        .setType(type)
        .setIdentifier(identifier);
    individualRepository.createIndividual(individual);
  }

  @Override
  public void deleteIndividual(String identifier, String name)
  {
    Individual toDelete = findIndividualByName(name,identifier);
    if(toDelete != null){
      individualRepository.deleteIndividual(toDelete);
    }
  }

  @Override
  public BigDecimal containsIndividual(String name)
  {
    return individualRepository.containsIndividual(name);
  }

  @Override
  public BigDecimal containsIdentifier(String identifier)
  {
    return individualRepository.containsIdentifier(identifier);
  }

  @Override
  public Individual findIndividualByName(String name, String lei)
  {
    return individualRepository.findIndividualByName(name, lei);
  }
}
