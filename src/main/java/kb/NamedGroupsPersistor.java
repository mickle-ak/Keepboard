package kb;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.util.ArrayList;
import java.util.List;

public class NamedGroupsPersistor extends ListPersistor<NamedItemGroup> {
	
	@Override
	protected List<XMLElement> createXmlList(List<NamedItemGroup> namedItemGroups) {
		List<XMLElement> result = new ArrayList<XMLElement>();
		for (NamedItemGroup namedItemGroup : namedItemGroups) {
			result.add(new XMLElement(namedItemGroup.getName(), namedItemGroup.getItemGroup().getFileName()));
		}
		return result;
	}

	@Override
	protected XStream createXStream() {
		XStream result = new XStream();
		result.addPermission(AnyTypePermission.ANY);
    	result.alias("item", XMLElement.class);
    	return result;
	}
	
	@Override
	protected List<NamedItemGroup> convertToItems(List<?> xmlList) {
		@SuppressWarnings("unchecked")
		List<XMLElement> tXmlList = (List<XMLElement>) xmlList; // Cast safety checked in superclass
		List<NamedItemGroup> result = new ArrayList<NamedItemGroup>(xmlList.size());
		for (XMLElement element : tXmlList) {
			result.add(new NamedItemGroup(element.name, element.fileName));
		}
		return result;
	}
	
	@Override
	protected Class<XMLElement> getXmlElementClass() {
		return XMLElement.class;
	}

	protected static class XMLElement {
		private String name;
		private String fileName;
		
		public XMLElement(String name, String fileName) {
			this.name = name;
			this.fileName = fileName;
		}
	}
}
