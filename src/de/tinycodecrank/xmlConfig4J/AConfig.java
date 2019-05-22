package de.tinycodecrank.xmlConfig4J;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileLock;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.tinycodecrank.xmlConfig4J.annotations.Value;

abstract class AConfig
{
	private static final String UTF_8 = "UTF8";
	private static final Logger log = LogManager.getLogger(AConfig.class.getName());
	private boolean mapTypes = true;
	
	protected void mapTypes(boolean doMap)
	{
		this.mapTypes = doMap;
	}

	/**
	 * Register old class-paths in here and associate them with the current one to
	 * enable loading config-files saved in previous versions of the program!
	 */
	private HashMap<String, Class<?>> classNameHistory = new HashMap<>();
	
	/**
	 * Used to shorten type information in the config tree
	 */
	private HashMap<Integer, String> typeMappings = null;
	private HashMap<String, Integer> reverseTypeMappings = null;
	
	/**
	 * @param type
	 * @return the id associated with type or type if there is no association
	 */
	private final String getMapping(String type)
	{
		if(mapTypes)
		{
			Integer id = reverseTypeMappings.get(type);
			if(id == null)
			{
				id = reverseTypeMappings.size();
				typeMappings.put(id, type);
				reverseTypeMappings.put(type, id);
			}
			return id.toString();
		}
		else
		{
			return type;
		}
	}
	
	private final String getType(String type)
	{
		if(NumberUtils.isParsable(type))
		{
			String tmp = typeMappings.get(Integer.valueOf(type));
			if(tmp != null)
			{
				return tmp;
			}
		}
		return type;
	}
	
	final void loadPrepared(InputStream stream) throws FileNotFoundException, IOException
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder parser = factory.newDocumentBuilder();
			Document document = parser.parse(stream);
			
			NodeList rootNodeList = document.getElementsByTagName("root");
			if(rootNodeList.getLength() > 0)
			{
				//Loads configs with typeMappings
				Node root = rootNodeList.item(0);
				NodeList nodes = root.getChildNodes();
				for(int i = 0; i < nodes.getLength(); i++)
				{
					Node node = nodes.item(i);
					if(node.getNodeName().equals("types"))
					{
						this.loadTypeMappings(node);
					}
				}
				for(int i = 0; i < nodes.getLength(); i++)
				{
					Node node = nodes.item(i);
					if(node.getNodeName().equals("config"))
					{
						this.loadObject(node, this);
					}
				}
			}
			else
			{
				//Loads configs without typeMappings
				NodeList configNodeList = document.getElementsByTagName("config");
				if (configNodeList.getLength() > 0)
				{
					Node nNode = configNodeList.item(0);
					this.loadObject(nNode, this);
				}
			}
		}
		catch (ParserConfigurationException | SAXException | ClassNotFoundException e)
		{
			log.error(e.getMessage(), e);
		}
	}
	
	private final void loadTypeMappings(Node mappings)
	{
		this.typeMappings = new HashMap<>();
		
		NodeList items = mappings.getChildNodes();
		for(int i = 0; i < items.getLength(); i++)
		{
			try
			{
				Node item = items.item(i);
				Attr idAttr = (Attr) item.getAttributes().getNamedItem("id");
				Attr typeAttr = (Attr) item.getAttributes().getNamedItem("name");
				Integer id = Integer.valueOf(idAttr.getValue());
				String type = typeAttr.getValue();
				if(id != null && type != null)
				{
					typeMappings.put(id, type);
				}
			}
			catch(NumberFormatException e)
			{
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * @param old
	 *            the old classPath
	 * @param current
	 *            the current classPath to map the old one to
	 * @return true if there wasn't yet an association and the given one was
	 *         successfully added This method is used to ensure that configs can
	 *         still be loaded after changing a class or its classpath. Therefore
	 *         the classPath of the old Class and a Class<?> object of the new one
	 *         must be provided!
	 */
	protected final boolean addAssociation(String old, Class<?> current)
	{
		if (this.classNameHistory.containsKey(old))
		{
			return false;
		}
		this.classNameHistory.put(old, current);
		return this.classNameHistory.containsKey(old);
	}

	final InputStream prepareInput(InputStream origStream) throws IOException
	{
		return prepareInput(origStream, null);
	}
	
	final InputStream prepareInput(InputStream origStream, FileLock lock) throws IOException
	{
		InputStreamReader iR = new InputStreamReader(origStream, UTF_8);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(iR);
		String line;
		while ((line = br.readLine()) != null)
		{
			sb.append(line.trim());
		}
		if(lock != null)
		{
			lock.release();
		}
		return new ByteArrayInputStream(sb.toString().getBytes(UTF_8));
	}

	private final void loadObject(Node parent, Object object) throws ClassNotFoundException
	{
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields)
		{
			if (field.isAnnotationPresent(Value.class))
			{
				NodeList nodeList = parent.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					String name = node.getNodeName();
					if (field.getName().equals(name))
					{
						field.setAccessible(true);
						try
						{
							Attr isNull = (Attr) node.getAttributes().getNamedItem("null");

							Attr type = (Attr) node.getAttributes().getNamedItem("type");
							String objectType;
							if(type != null)
							{
								objectType = type.getValue();
							}
							else
							{
								objectType = field.getType().getName();
							}
							objectType = getType(objectType);
							switch (objectType)
							{
								case "java.lang.String":
									if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
									{
										field.set(object,
												((Attr) node.getAttributes().getNamedItem("value")).getValue());
									}
									else
									{
										field.set(object, null);
									}
									break;
								case "boolean":
									field.set(object, Boolean.parseBoolean(
											((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									break;
								case "byte":
									field.set(object, Byte
											.parseByte(((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									break;
								case "char":
									field.set(object,
											((Attr) node.getAttributes().getNamedItem("value")).getValue().charAt(0));
									break;
								case "double":
									field.set(object, Double.parseDouble(
											((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									break;
								case "float":
									field.set(object, Float.parseFloat(
											((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									break;
								case "int":
									field.set(object, Integer
											.parseInt(((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									break;
								case "long":
									field.set(object, Long
											.parseLong(((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									break;
								case "short":
									field.set(object, Short.parseShort(
											((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									break;
								case "java.util.UUID":
									if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
									{
										long mostSigBits = Long.parseLong(
												((Attr) node.getAttributes().getNamedItem("mostSigBits")).getValue());
										long leastSigBits = Long.parseLong(
												((Attr) node.getAttributes().getNamedItem("leastSigBits")).getValue());
										UUID uuid = new UUID(mostSigBits, leastSigBits);
										field.set(object, uuid);
									}
									else
									{
										field.set(object, null);
									}
									break;
								case "java.lang.Boolean":
									if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
									{
										field.set(object, Boolean.parseBoolean(
												((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									}
									else
									{
										field.set(object, null);
									}
									break;
								case "java.lang.Byte":
									if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
									{
										field.set(object, Byte.parseByte(
												((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									}
									else
									{
										field.set(object, null);
									}
									break;
								case "java.lang.Short":
									if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
									{
										field.set(object, Short.parseShort(
												((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									}
									else
									{
										field.set(object, null);
									}
									break;
								case "java.lang.Character":
									if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
									{
										field.set(object, ((Attr) node.getAttributes().getNamedItem("value")).getValue()
												.charAt(0));
									}
									else
									{
										field.set(object, null);
									}
									break;
								case "java.lang.Integer":
									if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
									{
										field.set(object, Integer.parseInt(
												((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									}
									else
									{
										field.set(object, null);
									}
									break;
								case "java.lang.Long":
									if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
									{
										field.set(object, Long.parseLong(
												((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									}
									else
									{
										field.set(object, null);
									}
									break;
								case "java.lang.Float":
									if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
									{
										field.set(object, Float.parseFloat(
												((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									}
									else
									{
										field.set(object, null);
									}
									break;
								case "java.lang.Double":
									if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
									{
										field.set(object, Double.parseDouble(
												((Attr) node.getAttributes().getNamedItem("value")).getValue()));
									}
									else
									{
										field.set(object, null);
									}
									break;
								default: // Any other object
									if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
									{
										if (field.getType().isArray())
										{
											this.loadArray(field, node, object);
										}
										else if (field.getType().isEnum())
										{
											this.loadEnum(field, node, object);
										}
										else if (Collection.class.isAssignableFrom(field.getType()))
										{
											this.loadList(field, node, object);
										}
										else if (Map.class.isAssignableFrom(field.getType()))
										{
											this.loadMap(field, node, object);
										}
										else
										{
											Class<?> clazz;
											if(type != null)
											{
												clazz = Class.forName(objectType);
											}
											else
											{
												clazz = field.getType();
											}
											Object obj = clazz.getDeclaredConstructor().newInstance();
											field.set(object, obj);
											this.loadObject(node, obj);
										}
									}
									break;
							}
						}
						catch (IllegalArgumentException | IllegalAccessException | InstantiationException
								| NoSuchMethodException | SecurityException | InvocationTargetException e)
						{
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		}
	}

	private final void loadEnum(Field field, Node node, Object container)
			throws IllegalArgumentException, IllegalAccessException
	{
		int enumOrdinal = Integer.parseInt(((Attr) node.getAttributes().getNamedItem("value")).getValue());
		if (enumOrdinal >= 0 && enumOrdinal < field.getType().getEnumConstants().length)
		{
			field.set(container, field.getType().getEnumConstants()[enumOrdinal]);
		}
	}

	private final void loadArray(Field field, Node arrayNode, Object container) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		NodeList nodeList = arrayNode.getChildNodes();
		switch (field.getType().getName())
		{
			case "[Ljava.lang.String;":
			{
				String[] array = new String[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					array[i] = ((Attr) node.getAttributes().getNamedItem("value")).getValue();
				}
				field.set(container, array);
				break;
			}
			case "[Z":
			{
				boolean[] array = new boolean[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					array[i] = Boolean.parseBoolean(((Attr) node.getAttributes().getNamedItem("value")).getValue());
				}
				field.set(container, array);
				break;
			}
			case "[B":
			{
				byte[] array = new byte[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					array[i] = Byte.parseByte(((Attr) node.getAttributes().getNamedItem("value")).getValue());
				}
				field.set(container, array);
				break;
			}
			case "[C":
			{
				char[] array = new char[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					array[i] = ((Attr) node.getAttributes().getNamedItem("value")).getValue().charAt(0);
				}
				field.set(container, array);
				break;
			}
			case "[D":
			{
				double[] array = new double[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					array[i] = Double.parseDouble(((Attr) node.getAttributes().getNamedItem("value")).getValue());
				}
				field.set(container, array);
				break;
			}
			case "[F":
			{
				float[] array = new float[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					array[i] = Float.parseFloat(((Attr) node.getAttributes().getNamedItem("value")).getValue());
				}
				field.set(container, array);
				break;
			}
			case "[I":
			{
				int[] array = new int[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					array[i] = Integer.parseInt(((Attr) node.getAttributes().getNamedItem("value")).getValue());
				}
				field.set(container, array);
				break;
			}
			case "[J":
			{
				long[] array = new long[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					array[i] = Long.parseLong(((Attr) node.getAttributes().getNamedItem("value")).getValue());
				}
				field.set(container, array);
				break;
			}
			case "[S":
			{
				short[] array = new short[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					array[i] = Short.parseShort(((Attr) node.getAttributes().getNamedItem("value")).getValue());
				}
				field.set(container, array);
				break;
			}
			case "[Ljava.util.UUID;":
			{
				UUID[] array = new UUID[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++)
				{
					Node node = nodeList.item(i);
					long mostSigBits = Long
							.parseLong(((Attr) node.getAttributes().getNamedItem("mostSoigBits")).getValue());
					long leastSigBits = Long
							.parseLong(((Attr) node.getAttributes().getNamedItem("leastSigBits")).getValue());
					array[i] = new UUID(mostSigBits, leastSigBits);
				}
				field.set(container, array);
				break;
			}
			default:
			{
				try
				{
					String arrayType = getType(((Attr) arrayNode.getAttributes().getNamedItem("type")).getValue());
					
					Class<?> collectionType = null;
					try
					{
						collectionType = Class.forName(arrayType);
					}
					catch (ClassNotFoundException e)
					{
						log.info(String.format("Class not found: %s - searching for compatibility-mapping",
								arrayType));
						collectionType = this.classNameHistory.get(arrayType);
						if (collectionType == null)
						{
							log.error(e.getMessage(), e);
						}
					}
					Object[] array = (Object[]) Array.newInstance(collectionType, nodeList.getLength());
					for (int i = 0; i < nodeList.getLength(); i++)
					{
						Node node = nodeList.item(i);
						String type = getType(((Attr) (node.getAttributes().getNamedItem("type"))).getValue());
						try
						{
							array[i] = Class.forName(type).getDeclaredConstructor().newInstance();
						}
						catch (ClassNotFoundException e)
						{
							log.info(
									String.format("Class not found: %s - searching for compatibility-mapping", type));
							Class<?> itemClass = this.classNameHistory.get(type);
							if (itemClass != null)
							{
								array[i] = itemClass.getDeclaredConstructor().newInstance();
							}
							else
							{
								log.error(e.getMessage(), e);
							}
						}
						this.loadObject(node, array[i]);
					}
					field.set(container, array);
					break;
				}
				catch (InstantiationException | ClassNotFoundException e)
				{
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final void loadList(Field field, Node listNode, Object container)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException,
			SecurityException, IllegalArgumentException, InvocationTargetException
	{
		String listType = getType(((Attr) listNode.getAttributes().getNamedItem("type")).getValue());
		Class<?> listClass = null;
		try
		{
			listClass = Class.forName(listType);
		}
		catch (ClassNotFoundException e)
		{
			log.info(String.format("Class not found: %s - searching for compatibility-mapping", listType));
			listClass = this.classNameHistory.get(listType);
			if (listClass == null)
			{
				log.error(e.getMessage(), e);
			}
		}
		if(listClass != null)
		{
			Collection list = (Collection) listClass.getDeclaredConstructor().newInstance();
			field.set(container, list);
			NodeList nodeList = listNode.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node item = nodeList.item(i);
				Attr isNull = (Attr) item.getAttributes().getNamedItem("null");
				if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
				{
					String type = getType(((Attr) item.getAttributes().getNamedItem("type")).getValue());
					switch (type)
					{
						case "java.lang.String":
							list.add(((Attr) item.getAttributes().getNamedItem("value")).getValue());
							break;
						case "boolean":
							list.add(Boolean.parseBoolean(((Attr) item.getAttributes().getNamedItem("value")).getValue()));
							break;
						case "byte":
							list.add(Byte.parseByte(((Attr) item.getAttributes().getNamedItem("value")).getValue()));
							break;
						case "char":
							list.add(((Attr) item.getAttributes().getNamedItem("value")).getValue().charAt(0));
							break;
						case "double":
							list.add(Double.parseDouble(((Attr) item.getAttributes().getNamedItem("value")).getValue()));
							break;
						case "float":
							list.add(Float.parseFloat(((Attr) item.getAttributes().getNamedItem("value")).getValue()));
							break;
						case "int":
							list.add(Integer.parseInt(((Attr) item.getAttributes().getNamedItem("value")).getValue()));
							break;
						case "long":
							list.add(Long.parseLong(((Attr) item.getAttributes().getNamedItem("value")).getValue()));
							break;
						case "short":
							list.add(Short.parseShort(((Attr) item.getAttributes().getNamedItem("value")).getValue()));
							break;
						case "java.util.UUID":
						{
							long mostSigBits = Long
									.parseLong(((Attr) item.getAttributes().getNamedItem("mostSigBits")).getValue());
							long leastSigBits = Long
									.parseLong(((Attr) item.getAttributes().getNamedItem("leastSigBits")).getValue());
							;
							list.add(new UUID(mostSigBits, leastSigBits));
							break;
						}
						default:
							Class<?> itemType = null;
							try
							{
								itemType = Class.forName(type);
							}
							catch (ClassNotFoundException e)
							{
								log.info(
										String.format("Class not found: %s - searching for compatibility-mapping", type));
								itemType = this.classNameHistory.get(type);
								if (itemType == null)
								{
									log.error(e.getMessage(), e);
								}
							}
							if(itemType != null)
							{
								Constructor<?> constructor = itemType.getDeclaredConstructor();
								
								constructor.setAccessible(true);
								Object o = constructor.newInstance();
		
								list.add(o);
								for (Field tmp : o.getClass().getFields())
								{
									if (tmp.isAnnotationPresent(Value.class))
									{
										this.loadObject(item, o);
									}
								}
							}
							break;
					}
				}
				else
				{
					list.add(null);
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final void loadMap(Field field, Node mapNode, Object container)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException
	{
		String mapType = getType(((Attr) mapNode.getAttributes().getNamedItem("type")).getValue());
		Class<?> mapClass = null;
		try
		{
			mapClass = Class.forName(mapType);
		}
		catch (ClassNotFoundException e)
		{
			log.info(String.format("Class not found: %s - searching for compatibility-mapping", mapType));
			mapClass = this.classNameHistory.get(mapType);
			if (mapClass == null)
			{
				log.error(e.getMessage(), e);
			}
		}
		if(mapClass != null)
		{
			Map map = (Map) mapClass.getDeclaredConstructor().newInstance();
			field.set(container, map);
			NodeList nodeList = mapNode.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node item = nodeList.item(i);
				NodeList itemList = item.getChildNodes();
				Node keyNode = null, valueNode = null;
				for (int j = 0; j < 2; j++)
				{
					if (itemList.item(j).getNodeName().equals("key"))
					{
						keyNode = itemList.item(j);
					}
					else
					{
						valueNode = itemList.item(j);
					}
				}
				Object key = null;
				if (keyNode != null && valueNode != null)
				{
					Attr isNull = (Attr) keyNode.getAttributes().getNamedItem("null");
					if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
					{
						String keyType = getType(((Attr) keyNode.getAttributes().getNamedItem("type")).getValue());
						switch (keyType)
						{
							case "null":
								break;
							case "java.lang.String":
								key = ((Attr) keyNode.getAttributes().getNamedItem("value")).getValue();
								break;
							case "boolean":
								key = Boolean
										.parseBoolean(((Attr) keyNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "byte":
								key = Byte.parseByte(((Attr) keyNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "char":
								key = ((Attr) keyNode.getAttributes().getNamedItem("value")).getValue().charAt(0);
								break;
							case "double":
								key = Double.parseDouble(((Attr) keyNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "float":
								key = Float.parseFloat(((Attr) keyNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "int":
								key = Integer.parseInt(((Attr) keyNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "long":
								key = Long.parseLong(((Attr) keyNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "short":
								key = Short.parseShort(((Attr) keyNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "java.util.UUID":
							{
								long mostSigBits = Long
										.parseLong(((Attr) keyNode.getAttributes().getNamedItem("mostSigBits")).getValue());
								long leastSigBits = Long.parseLong(
										((Attr) keyNode.getAttributes().getNamedItem("leastSigBits")).getValue());
								key = new UUID(mostSigBits, leastSigBits);
								break;
							}
							default:
								Class<?> keyClass = null;
								try
								{
									keyClass = Class.forName(keyType);
								}
								catch (ClassNotFoundException e)
								{
									log.info(String.format("Class not found: %s - searching for compatibility-mapping",
											keyType));
									keyClass = this.classNameHistory.get(keyType);
									if (keyClass == null)
									{
										log.error(e.getMessage(), e);
									}
								}
								if(keyClass != null)
								{
									key = keyClass.getDeclaredConstructor().newInstance();
									for (Field tmp : key.getClass().getFields())
									{
										if (tmp.isAnnotationPresent(Value.class))
										{
											this.loadObject(keyNode, key);
										}
									}
								}
								break;
						}
					}
	
					Object value = null;
					if (isNull == null || !isNull.getValue().equalsIgnoreCase("true"))
					{
						String valueType = getType(((Attr) valueNode.getAttributes().getNamedItem("type")).getValue());
						switch (valueType)
						{
							case "null":
								break;
							case "java.lang.String":
								value = ((Attr) valueNode.getAttributes().getNamedItem("value")).getValue();
								break;
							case "boolean":
								value = Boolean
										.parseBoolean(((Attr) valueNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "byte":
								value = Byte.parseByte(((Attr) valueNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "char":
								value = ((Attr) valueNode.getAttributes().getNamedItem("value")).getValue().charAt(0);
								break;
							case "double":
								value = Double
										.parseDouble(((Attr) valueNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "float":
								value = Float
										.parseFloat(((Attr) valueNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "int":
								value = Integer
										.parseInt(((Attr) valueNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "long":
								value = Long.parseLong(((Attr) valueNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "short":
								value = Short
										.parseShort(((Attr) valueNode.getAttributes().getNamedItem("value")).getValue());
								break;
							case "java.util.UUID":
							{
								long mostSigBits = Long.parseLong(
										((Attr) valueNode.getAttributes().getNamedItem("mostSigBits")).getValue());
								long leastSigBits = Long.parseLong(
										((Attr) valueNode.getAttributes().getNamedItem("leastSigBits")).getValue());
								value = new UUID(mostSigBits, leastSigBits);
								break;
							}
							default:
								Class<?> valueClass = null;
								try
								{
									valueClass = Class.forName(valueType);
								}
								catch (ClassNotFoundException e)
								{
									log.info(String.format("Class not found: %s - searching for compatibility-mapping",
											valueType));
									valueClass = this.classNameHistory.get(valueType);
									if (valueClass == null)
									{
										log.error(e.getMessage(), e);
									}
								}
								if(valueClass != null)
								{
									value = valueClass.getDeclaredConstructor().newInstance();
									for (Field tmp : value.getClass().getFields())
									{
										if (tmp.isAnnotationPresent(Value.class))
										{
											this.loadObject(valueNode, value);
										}
									}
								}
								break;
						}
					}
					map.put(key, value);
				}
			}
		}
	}

	final void save(StreamResult output)
	{
		try
		{
			this.typeMappings = new HashMap<>();
			this.reverseTypeMappings = new HashMap<>();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			Document document = parser.newDocument();
			Element rootElement = document.createElement("root");
			Element configElement = document.createElement("config");

			for (Field field : this.getClass().getDeclaredFields())
			{
				if (field.isAnnotationPresent(Value.class))
				{
					this.saveObject(this, field, configElement, document);
				}
			}
			Class<?> tmpClass = this.getClass();
			while((tmpClass = tmpClass.getSuperclass()) != null && tmpClass != Object.class)
			{
				for(Field tmp : tmpClass.getDeclaredFields())
				{
					if(tmp.isAnnotationPresent(Value.class))
					{
						this.saveObject(this, tmp, configElement, document);
					}
				}
			}
			
			if(mapTypes)
			{
				rootElement.appendChild(saveTypeMappings(document));
			}
			rootElement.appendChild(configElement);
			document.appendChild(rootElement);
			document.normalizeDocument();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			Source input = new DOMSource(document);
			transformer.transform(input, output);

		}
		catch (ParserConfigurationException | TransformerFactoryConfigurationError | TransformerException
				| IllegalArgumentException | DOMException | IllegalAccessException e)
		{
			log.error(e.getMessage(), e);
		}
	}
	
	private final Element saveTypeMappings(Document document)
	{
		Element mapping = document.createElement("types");
		for(int i = 0; i < this.typeMappings.size(); i++)
		{
			String typeName = this.typeMappings.get(Integer.valueOf(i));
			Element type = document.createElement("item");
			type.setAttribute("id", Integer.toString(i));
			type.setAttribute("name", typeName);
			mapping.appendChild(type);
		}
		return mapping;
	}

	private final void saveObject(Object container, Field field, Element parent, Document document)
			throws DOMException, IllegalArgumentException, IllegalAccessException
	{
		Value value = field.getAnnotation(Value.class);
		if (value == null)
		{
			return;
		}
		field.setAccessible(true);

		Element element = document.createElement(field.getName());
		if (value.value() != null && !value.value().isEmpty())
		{
			element.setAttribute("info", value.value());
		}

		final String fieldTypeName = field.getType().getName();
		switch (fieldTypeName)
		{
			case "boolean":
			case "byte":
			case "char":
			case "double":
			case "float":
			case "int":
			case "long":
			case "short":
				element.setAttribute("value", field.get(container).toString());
				break;
			case "java.lang.Boolean":
			case "java.lang.Byte":
			case "java.lang.Short":
			case "java.lang.Character":
			case "java.lang.Integer":
			case "java.lang.Long":
			case "java.lang.Float":
			case "java.lang.Double":
			case "java.lang.String":
				Object primitiveObj = field.get(container);
				if (primitiveObj == null)
				{
					element.setAttribute("null", "true");
				}
				else
				{
					element.setAttribute("value", primitiveObj.toString());
				}
				break;
			case "java.util.UUID":
			{
				Object uuid = field.get(container);
				if (uuid == null)
				{
					element.setAttribute("null", "true");
				}
				else
				{
					element.setAttribute("mostSigBits", Long.toString(((UUID) uuid).getMostSignificantBits()));
					element.setAttribute("leastSigBits", Long.toString(((UUID) uuid).getLeastSignificantBits()));
				}
				break;
			}
			default: // Any other object
				if (field.getType().isArray())
				{
					this.saveArray(container, field, element, document);
				}
				else if (field.getType().isEnum())
				{
					this.saveEnum(container, field, element, document);
				}
				else if (Collection.class.isAssignableFrom(field.getType()))
				{
					this.saveList(container, field, element, document);
				}
				else if (Map.class.isAssignableFrom(field.getType()))
				{
					this.saveMap(container, field, element, document);
				}
				else
				{
					Object object = field.get(container);
					if (object == null)
					{
						element.setAttribute("null", "true");
					}
					else
					{
						String typeName = object.getClass().getTypeName();
						if(!typeName.equals(fieldTypeName))
						{
							element.setAttribute("type", getMapping(typeName));
						}
						for (Field tmp : object.getClass().getDeclaredFields())
						{
							if (field.isAnnotationPresent(Value.class))
							{
								this.saveObject(object, tmp, element, document);
							}
						}
						Class<?> tmpClass = object.getClass();
						while((tmpClass = tmpClass.getSuperclass()) != null && tmpClass != Object.class)
						{
							for(Field tmp : tmpClass.getDeclaredFields())
							{
								if(tmp.isAnnotationPresent(Value.class))
								{
									this.saveObject(object, tmp, element, document);
								}
							}
						}
					}
				}
				break;
		}
		parent.appendChild(element);
	}

	private final void saveEnum(Object container, Field field, Element element, Document document)
			throws DOMException, IllegalArgumentException, IllegalAccessException
	{
		element.setAttribute("value", Integer.toString(((Enum<?>) field.get(container)).ordinal()));
	}

	private final void saveArray(Object container, Field field, Element element, Document document)
			throws IllegalArgumentException, IllegalAccessException
	{
		Object array = field.get(container);
		if (array == null)
		{
			element.setAttribute("null", "true");
		}
		else
		{
			switch (field.getType().getName())
			{
				case "[Ljava.lang.String;":
				case "[Z":
				case "[B":
				case "[C":
				case "[D":
				case "[F":
				case "[I":
				case "[J":
				case "[S":
				{
					for (int i = 0; i < Array.getLength(array); i++)
					{
						Element item = document.createElement("item");
						item.setAttribute("value", Array.get(array, i).toString());
						element.appendChild(item);
					}
					break;
				}
				case "[Ljava.util.UUID;":
				{
					for (int i = 0; i < Array.getLength(array); i++)
					{
						Element item = document.createElement("item");
						UUID uuid = (UUID) Array.get(array, i);
						if (uuid != null)
						{
							item.setAttribute("mostSigBits", Long.toString(uuid.getMostSignificantBits()));
							item.setAttribute("leastSigBits", Long.toString(uuid.getLeastSignificantBits()));
						}
						else
						{
							item.setAttribute("null", "true");
						}
					}
					break;
				}
				default:
				{
					String type = getMapping(field.getType().getComponentType().getName());
					element.setAttribute("type", type);
					for (int i = 0; i < Array.getLength(array); i++)
					{
						Element item = document.createElement("item");
						type = getMapping(array.getClass().getComponentType().getName());
						item.setAttribute("type", type);
						Object obj = Array.get(array, i);
						for (Field tmp : obj.getClass().getDeclaredFields())
						{
							if (tmp.isAnnotationPresent(Value.class))
							{
								this.saveObject(obj, tmp, item, document);
							}
						}
						element.appendChild(item);
					}
					break;
				}
			}
		}
	}

	private final void saveList(Object container, Field field, Element element, Document document)
			throws IllegalArgumentException, IllegalAccessException
	{
		Collection<?> list = (Collection<?>) field.get(container);
		if (list == null)
		{
			element.setAttribute("null", "true");
		}
		else
		{
			String listType = getMapping(list.getClass().getTypeName());
			element.setAttribute("type", listType);
			for (Object o : list)
			{
				if(o != null)
				{
					String typeName = o.getClass().getTypeName();
					Element item = document.createElement("item");
					item.setAttribute("type", getMapping(typeName));
					switch (typeName)
					{
						case "java.lang.String":
						case "boolean":
						case "byte":
						case "char":
						case "double":
						case "float":
						case "int":
						case "long":
						case "short":
							item.setAttribute("value", o.toString());
							break;
						case "java.util.UUID":
						{
							UUID uuid = (UUID) o;
							long mostSigBits = uuid.getMostSignificantBits();
							long leastSigBits = uuid.getLeastSignificantBits();
							item.setAttribute("mostSigBits", Long.toString(mostSigBits));
							item.setAttribute("leastSigBits", Long.toString(leastSigBits));
							break;
						}
						default:
							for (Field tmp : o.getClass().getDeclaredFields())
							{
								if (tmp.isAnnotationPresent(Value.class))
								{
									this.saveObject(o, tmp, item, document);
								}
							}
							break;
					}
					element.appendChild(item);
				}
				else
				{
					Element item = document.createElement("item");
					item.setAttribute("null", "true");
					element.appendChild(item);
				}
			}
		}
	}

	private final void saveMap(Object container, Field field, Element element, Document document)
			throws IllegalArgumentException, IllegalAccessException
	{
		Map<?, ?> map = (Map<?, ?>) field.get(container);
		if (map == null)
		{
			element.setAttribute("null", "true");
		}
		else
		{
			String mapType = map.getClass().getTypeName();
			element.setAttribute("type", getMapping(mapType));
			for (Entry<?, ?> entry : map.entrySet())
			{
				String keyType;
				if (entry.getKey() != null)
				{
					keyType = entry.getKey().getClass().getTypeName();
				}
				else
				{
					keyType = "null";
				}
				String valueType;
				if (entry.getValue() != null)
				{
					valueType = entry.getValue().getClass().getTypeName();
				}
				else
				{
					valueType = "null";
				}
				Element item = document.createElement("item");
				Element key = document.createElement("key");
				key.setAttribute("type", getMapping(keyType));
				Element value = document.createElement("value");
				value.setAttribute("type", getMapping(valueType));
				switch (keyType)
				{
					case "null":
						break;
					case "java.lang.String":
					case "boolean":
					case "byte":
					case "char":
					case "double":
					case "float":
					case "int":
					case "long":
					case "short":
						key.setAttribute("value", entry.getKey().toString());
						break;
					case "java.util.UUID":
					{
						UUID uuid = (UUID) entry.getKey();
						long mostSigBits = uuid.getMostSignificantBits();
						long leastSigBits = uuid.getLeastSignificantBits();
						key.setAttribute("mostSigBits", Long.toString(mostSigBits));
						key.setAttribute("leastSigBits", Long.toString(leastSigBits));
						break;
					}
					default:
						for (Field tmp : entry.getKey().getClass().getDeclaredFields())
						{
							if (tmp.isAnnotationPresent(Value.class))
							{
								this.saveObject(entry.getKey(), tmp, key, document);
							}
						}
						break;
				}
				switch (valueType)
				{
					case "null":
						break;
					case "java.lang.String":
					case "boolean":
					case "byte":
					case "char":
					case "double":
					case "float":
					case "int":
					case "long":
					case "short":
						value.setAttribute("value", entry.getValue().toString());
						break;
					case "java.util.UUID":
					{
						UUID uuid = (UUID) entry.getValue();
						long mostSigBits = uuid.getMostSignificantBits();
						long leastSigBits = uuid.getLeastSignificantBits();
						value.setAttribute("mostSigBits", Long.toString(mostSigBits));
						value.setAttribute("leastSigBits", Long.toString(leastSigBits));
						break;
					}
					default:
						for (Field tmp : entry.getValue().getClass().getDeclaredFields())
						{
							if (tmp.isAnnotationPresent(Value.class))
							{
								this.saveObject(entry.getValue(), tmp, value, document);
							}
						}
						break;
				}
				item.appendChild(key);
				item.appendChild(value);
				element.appendChild(item);
			}
		}
	}
}