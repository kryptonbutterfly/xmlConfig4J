# xmlConfig4J
A Simple to use Library for Object de-/serialization to xml-Format

## Download

[**xmlConfig4J.jar**](https://github.com/tinycodecrank/xmlConfig4J/releases/download/v1.0.1/xmlConfig4J.jar)

## Dependencies
[apache / **commons-lang**](https://commons.apache.org/proper/commons-lang/download_lang.cgi)

[apache / **logging-log4j2**](https://logging.apache.org/log4j/2.x/download.html)

## License

This project is licensed under Apache License 2.0 - see the [LICENSE](https://github.com/tinycodecrank/xmlConfig4J/blob/master/LICENSE) file for details

## Contributing

Please do contribute!  
Any help with this project is much appreciated.

## Example

The class TinyExample:
```java
public class TinyExample extends FileConfig{
  public TinyExample(){
    super(new File("./config.xml"));
  }
  
  @Value("List of favorite foods.")
  public ArrayList<String> favoriteFoods = genFavFoods();
  
  @Value
  public int someNumber = 1337;
  
  @Value
  public CustomClass cc = new CustomClass();
  
  private ArrayList<String> genFavFoods(){
    ArrayList<String> list = new ArrayList<>();
    list.add("Banana");
    list.add("Apple");
    list.add("Cheese");
    return list;
  }
  
  public static void main(String[] args) throws IOException{
    TinyExample config = new TinyExample();
    if(!config.exists()){
      config.save();  //write content of annotated fields to the specified file
    } else{
      config.load();  //read from the specified file and sets the annotated fields according
      config.favoriteFoods.forEach(System.out::println);
      System.out.println(config.someNumber);
      System.out.println("PI: " + config.cc.pi);
    }
  }

  public static class CustomClass{
    @Value
    public double pi = Math.PI;
  }
}
```
generates the config File ./config.xml :

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<root>
  <types>
    <item id="0" name="java.util.ArrayList"/>
    <item id="1" name="java.lang.String"/>
  </types>
  <config>
    <favoriteFoods info="List of favourite foods." type="0">
      <item type="1" value="Banana"/>
      <item type="1" value="Apple"/>
      <item type="1" value="Cheese"/>
    </favoriteFoods>
    <someNumber value="1337"/>
    <cc>
      <pi value="3.141592653589793"/>
    </cc>
  </config>
</root>
```
