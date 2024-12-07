package org.folio.rest.camunda.utility;

public class MappingParametersDescriptor<C, P> {

  private final Class<C> collectionType;

  private final String path;

  private MappingParametersDescriptor(Class<C> collectionType, String path) {
    this.collectionType = collectionType;
    this.path = path;
  }

  C getParameters(OkapiRestTemplate okapiRestRemplate) {
    return okapiRestRemplate.getForEntity(path, collectionType).getBody();
  }

  public static <C, P> MappingParametersDescriptor<C, P> of(Class<C> collectionType, String path) {
    return new MappingParametersDescriptor<C, P>(collectionType, path);
  }

}
