<img width="82" align="left"
src="https://raw.githubusercontent.com/kryptonbutterfly/xmlConfig4J/master/md/icon.svg"/>

# xmlConfig4J [![Maven Package](https://github.com/kryptonbutterfly/xmlConfig4J/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/kryptonbutterfly/xmlConfig4J/actions/workflows/maven-publish.yml)

A Simple to use Library for Object de-/serialization to xml-Format.

## What is supported

Not only tree shaped data structures, but acyclic as well as cyclic graphs.
The intrisically supported java types are:

* primitive types
* boxed types of primitives
* types implementing `java.util.List`, `java.util.Set` and `java.util.Map`
* all enums
* all records
* `java.awt.Color`
* `java.lang.String`
* `java.util.UUID`
* Any class with an empty or default constructor will de-/serialize all declared fields annotated with `@Value` or an equivalent registered annotation.
* any array of a supported type, including arrays of arrays …

In addition it is possible to implement a new `TypeAdapter` and register it, in order to support more types.

## Getting the latest release

```xml
<repository>
  <id>github</id>
  <url>https://maven.pkg.github.com/kryptonbutterfly/maven-repo</url>
</repository>
```
```xml
<dependency>
  <groupId>kryptonbutterfly</groupId>
  <artifactId>xml_config_4j</artifactId>
  <version>4.1.0</version>
</dependency>
```

## Download

<table>
  <tr>
    <th>library version</th>
    <th align="center">Download</th>
    <th align="center">java version</th>
  </tr>
  <tr>
    <td>4.1.0</td>
    <td align="center" valign="center"><a href="https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v4.1.0/xmlConfig4J.jar"><b>xmlConfig4J.jar</b></a></td>
    <td align="center">21+</td>
  </tr>
  <tr>
    <td>4.0.0</td>
    <td align="center" valign="center"><a href="https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v4.0.0/xmlConfig4J.jar"><b>xmlConfig4J.jar</b></a></td>
    <td align="center">21+</td>
  </tr>
  <tr>
    <td>3.2.0</td>
    <td align="center" valign="center"><a href="https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v3.2.0/xmlConfig4J.jar"><b>xmlConfig4J.jar</b></a></td>
    <td align="center">18+</td>
  </tr>
  <tr>
    <td>3.1.0&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="https://github.com/kryptonbutterfly/xmlConfig4J/releases/tag/v3.2.0"><img width="28" src="./md/exclamation_mark.svg" title="DEPRECATED: Don't use this version, use v3.2.0 instead!"/></a></td>
    <td align="center"><a href="https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v3.1.0/xmlConfig4J.jar"><b>xmlConfig4J.jar</b></a></td>
    <td align="center">18+</td>
  </tr>
  <tr>
    <td>2.2.0</td>
    <td><a href="https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v2.2.0/xmlConfig4J.jar"><b>xmlConfig4J.jar</b></a></td>
    <td align="center">18+</td>
  </tr>
  <tr>
    <td>2.1.0</td>
    <td><a href="https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v2.1.0/xmlConfig4J.jar"><b>xmlConfig4J.jar</b></a></td>
    <td align="center">18+</td>
  </tr>
  <tr>
    <td>2.0.7</td>
    <td><a href="https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v2.0.7/xmlConfig4J.jar"><b>xmlConfig4J.jar</b></a></td>
    <td align="center">18+</td>
  </tr>
  <tr>
    <td>2.0.4</td>
    <td><a href="https://github.com/kryptonbutterfly/xmlConfig4J/releases/download/v2.0.4/xmlConfig4J.jar"><b>xmlConfig4J.jar</b></a></td>
    <td align="center">8+</td>
  </tr>
</table>

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
public class TinyExample
{
  public static final XmlDataBinding xdb = new BindingBuilder()
    .indent(true)
    .indent(2)
    .addTypeAdapter(new ColorAdapter())
    .mapTypes(true)
    .build();
  
  public static final File config = new File("./config.xml");
  
  @Value("List of favorite foods.")
  public ArrayList<String> favoriteFoods = genFavFoods();
  
  @Value
  public int someNumber = 1337;
  
  @Value
  public CustomClass cc = new CustomClass();
  
  @Value
  public CustomClass cc2 = cc;
  
  @Value
  public HashMap<String, String> map = new HashMap<>();
  {
    map.put("A", "a");
    map.put("B", null);
    map.put(null, "A");
  }
  
  @Value
  public String[][] array = new String[][] { { "Banana", null }, null, {}, { "Apple", "Orange" } };
  
  @Value
  public HashSet<String> set = new HashSet<>();
  {
    set.add("A");
    set.add("B");
    set.add("C");
    set.add(null);
  }
  
  public static class CustomClass
  {
    @Value
    public double pi = Math.PI;
  }
  
  private ArrayList<String> genFavFoods()
  {
    var list = new ArrayList<String>();
    list.add("Banana");
    list.add("Apple");
    list.add(null);
    list.add("Cheese");
    return list;
  }
  
  public static void main(String[] args) throws Exception
  {
    final var builder = new PersistableResourceBuilder<TinyExample>(xdb, false, TinyExample.class);
    try (final var resource = builder.fromFile(config, TinyExample::new))
    {
      final var data = resource.data();
      System.out.printf(
        "List:\n\t%s\n\nsomeNumber:\n\t%s\n\nPI:\n\t%s\n\nMap:\n\t%s\n\nSet:\n\t%s\n\nArray:\n\t%s\n\ncc == cc2 : %s\n\n",
        data.favoriteFoods.stream().reduce((a, b) -> a + "\n\t" + b).orElse(""),
        data.someNumber,
        data.cc.pi,
        data.map.entrySet().stream().map(Objects::toString).reduce((a, b) -> a + "\n\t" + b).orElse(""),
        data.set.stream().map(Objects::toString).reduce((a, b) -> a + "\n\t" + b).orElse(""),
        Arrays.deepToString(data.array),
        data.cc == data.cc2);
    }
  }
}
```
generates the config File ./config.xml :

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<root>
  <types>
    <item id="1" name="java.lang.String"/>
    <item id="0" name="example.TinyExample"/>
    <item id="2" name="[Ljava.lang.String;"/>
  </types>
  <data type="0">
    <favoriteFoods info="List of favorite foods.">
      <item type="1" value="Banana"/>
      <item type="1" value="Apple"/>
      <item null="true"/>
      <item type="1" value="Cheese"/>
    </favoriteFoods>
    <someNumber value="1337"/>
    <cc inst-id="0">
      <pi value="3.141592653589793"/>
    </cc>
    <cc2 ref-id="0"/>
    <map>
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
      <item type="2">
        <item type="1" value="Banana"/>
        <item null="true"/>
      </item>
      <item null="true"/>
      <item type="2"/>
      <item type="2">
        <item type="1" value="Apple"/>
        <item type="1" value="Orange"/>
      </item>
    </array>
    <set>
      <item null="true"/>
      <item type="1" value="A"/>
      <item type="1" value="B"/>
      <item type="1" value="C"/>
    </set>
  </data>
</root>
```
