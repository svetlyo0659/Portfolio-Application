package bg.codeacademy.spring.application.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Individual
{

  @JsonIgnore
  private String id;
  private String name;
  private String type;
  private String identifier;

  public Individual()
  {
    //default Const.
  }

  @JsonIgnore
  public String getId()
  {
    return id;
  }

  public Individual setId(String id)
  {
    this.id = id;
    return this;
  }

  public String getName()
  {
    return name;
  }

  public Individual setName(String name)
  {
    this.name = name;
    return this;
  }

  public String getType()
  {
    return type;
  }

  public Individual setType(String type)
  {
    this.type = type;
    return this;
  }

  public String getIdentifier()
  {
    return identifier;
  }

  public Individual setIdentifier(String identifier)
  {
    this.identifier = identifier;
    return this;
  }
}
