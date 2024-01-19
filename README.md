# xmlConfig4J [![Maven Package](https://github.com/kryptonbutterfly/xmlConfig4J/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/kryptonbutterfly/xmlConfig4J/actions/workflows/maven-publish.yml)

A Simple to use Library for Object de-/serialization to xml-Format

## Getting the latest release

```xml
<repository>
  <id>github</id>
  <url>https://maven.pkg.github.com/kryptonbutterfly/maven-repo</url>
</repository>
```
```xml
<dependency>
  <groupId>de.tinycodecrank</groupId>
  <artifactId>xml_config_4j<artifactId>
  <version>2.2.0</version>
</dependency>
```

## Download

java version | library version | Download
:----------: | :-------------: | --------
18+          | 2.2.0           | [**xmlConfig4J.jar**](https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v2.2.0/xmlConfig4J.jar)
18+          | 2.1.0           | [**xmlConfig4J.jar**](https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v2.1.0/xmlConfig4J.jar)
18+          | 2.0.7           | [**xmlConfig4J.jar**](https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v2.0.7/xmlConfig4J.jar)
8+           | 2.0.4           | [**xmlConfig4J.jar**](https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v2.0.4/xmlConfig4J.jar)

## Dependencies

#### version > 2.1.0
* use maven

#### version 2.1.0
* [**ReflectionUtils.jar**](https://github.com/kryptonbutterfly/ReflectionUtils/releases/download/v1.0.0/ReflectionUtils.jar)

#### version 2.0.5 - 2.0.7
–

#### version < 2.0.5
* [apache / **commons-lang**](https://commons.apache.org/proper/commons-lang/download_lang.cgi)
* [apache / **logging-log4j2**](https://logging.apache.org/log4j/2.x/download.html)

## License

This project is licensed under Apache License 2.0 - see the [LICENSE](https://github.com/kryptonbutterfly/xmlConfig4J/blob/master/LICENSE) file for details

## Contributing

Please do contribute!
Any help with this project is greatly appreciated.

## Example

The class TinyExample:

```java
public class TinyExample extends FileConfig
{
  public TinyExample()
  {
    super(new File("./config.xml"));
  }
  
  @Value("List of favorite foods.")
  public ArrayList<String> favoriteFoods = genFavFoods();
  
  @Value
  public int someNumber = 1337;
  
  @Value
  public CustomClass cc = new CustomClass();

  @Value
  public HashMap<String, String> map = new HashMap<>();
  {
    map.put("A", "a");
    map.put("B", null);
    map.put(null, "A");
  }
  
  @Value
  public String[][] array = new String[][] {{"Banana", null}, null, {}, {"Apple", "Orange"}};
  
  @Value
  public HashSet<String> set = new HashSet<>();
  {
    set.add("A");
    set.add("B");
    set.add("C");
    set.add(null);
  }
  
  private ArrayList<String> genFavFoods()
  {
    ArrayList<String> list = new ArrayList<>();
    list.add("Banana");
    list.add("Apple");
    list.add(null);
    list.add("Cheese");
    return list;
  }
  
  public static void main(String[] args) throws IOException
  {
    TinyExample config = new TinyExample();
    if(config.exists())
    {
      config.load();
      
      System.out.println("List:");
      config.favoriteFoods.stream().map(s -> "\t" + s).forEach(System.out::println);
      
      System.out.printf("%nsomeNumber:%n\t%s%n", config.someNumber);
      
      System.out.printf("%nPi:%n\t%s%n", config.cc.pi);
      
      System.out.printf("%nMap:%n");
      config.map.entrySet().stream().map(s -> "\t" + s).forEach(System.out::println);
      
      System.out.printf("%nSet:%n");
      config.set.stream().map(s -> "\t" + s).forEach(System.out::println);
      
      System.out.printf("%nArray:%n%s%n", Arrays.deepToString(config.array));
    }
    config.save();
  }
  
  public static class CustomClass
  {
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
    <item id="4" name="java.util.HashSet"/>
    <item id="2" name="java.util.HashMap"/>
    <item id="1" name="java.lang.String"/>
    <item id="3" name="[Ljava.lang.String;"/>
    <item id="0" name="java.util.ArrayList"/>
  </types>
  <config>
    <favoriteFoods info="List of favorite foods." type="0">
      <item type="1" value="Banana"/>
      <item type="1" value="Apple"/>
      <item null="true"/>
      <item type="1" value="Cheese"/>
    </favoriteFoods>
    <someNumber value="1337"/>
    <cc>
      <pi value="3.141592653589793"/>
    </cc>
    <map type="2">
      <item>
        <key null="true"/>
        <value type="1" value="A"/>
      </item>
      <item>
        <key type="1" value="A"/>
        <value type="1" value="a"/>
      </item>
      <item>
        <key type="1" value="B"/>
        <value null="true"/>
      </item>
    </map>
    <array>
      <item type="3">
        <item type="1" value="Banana"/>
        <item null="true"/>
      </item>
      <item null="true"/>
      <item type="3"/>
      <item type="3">
        <item type="1" value="Apple"/>
        <item type="1" value="Orange"/>
      </item>
    </array>
    <set type="4">
      <item null="true"/>
      <item type="1" value="A"/>
      <item type="1" value="B"/>
      <item type="1" value="C"/>
    </set>
  </config>
</root>
```
