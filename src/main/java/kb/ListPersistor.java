package kb;

import com.thoughtworks.xstream.XStream;
import kb.utils.ObjectIO;
import kb.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public abstract class ListPersistor<T> {

	public final void storeToDisc(List<T> elements, String fileName) {
		String xmlElements = getXML(elements);
		new ObjectIO().writeText(xmlElements, fileName);
		new ObjectIO().writeText(xmlElements, fileName + ".backup");
	}
	
	private String getXML(List<T> elements) {
		return createXStream().toXML(createXmlList(elements));
	}
	
	protected abstract List<?> createXmlList(List<T> elements);
	
	public final List<T> readFromDisk(String fileName) {
		 List<T> result = doReadFromDisk(fileName);
		 if (result.isEmpty()) {
			 return doReadFromDisk(fileName + ".backup");
		 }
		 return result;
	}

	private List<T> doReadFromDisk(String fileName) {
		String xml = new ObjectIO().readText(fileName);
		
		if (xml == null || xml.trim().isEmpty()) {
			return emptyList();
		}
		
		return convertToItems(fromXml(xml));
	}
	
	protected abstract List<T> convertToItems(List<?> xmlList);

	private List<?> fromXml(String xml) {
		try {
			List<?> result = (List<?>) createXStream().fromXML(xml);
			
			if (!Utils.areAllElementsCorrectlyTyped(result, getXmlElementClass())) {
				return emptyList();
			}
			
			return result;
		} catch (Throwable e) {
			return emptyList();
		}
	}
	
	protected abstract Class<?> getXmlElementClass();
	
	protected static <U> List<U> emptyList() {
		return new ArrayList<U>();
	}

	protected abstract XStream createXStream();
	
	public final void delete(final String fileName) {
		new ObjectIO().delete(fileName);
		new ObjectIO().delete(fileName + ".backup");
	}
}
