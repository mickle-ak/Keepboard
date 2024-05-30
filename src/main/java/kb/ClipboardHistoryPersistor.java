package kb;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;

import java.util.ArrayList;
import java.util.List;

public class ClipboardHistoryPersistor extends ListPersistor<ClipboardItem> {
	
	@Override
	protected List<XMLElement> createXmlList(List<ClipboardItem> items) {
		List<XMLElement> result = new ArrayList<XMLElement>();
		for (ClipboardItem item : items) {
			result.add(createXMLElement(item));
		}
		return result;
	}

	private XMLElement createXMLElement(ClipboardItem item) {
		if (item instanceof TextClipboardItem) {
			return new XMLElement(item.getName(), item.getFileName(), item.getDisplayText(),
					((TextClipboardItem) item).getShortText(), null, null);
		} else {
			return new XMLElement(item.getName(), item.getFileName(), item.getDisplayText(), null,
					((ImageClipboardItem) item).getPreviewFileName(), ((ImageClipboardItem) item).getHash());
		}
	}

	@Override
	protected XStream createXStream() {
		XStream result = new XStream();
		result.addPermission(AnyTypePermission.ANY);
    	result.alias("item", XMLElement.class);
    	return result;
	}
	
	@Override
	protected List<ClipboardItem> convertToItems(List<?> xmlList) {
		@SuppressWarnings("unchecked")
		List<XMLElement> tXmlList = (List<XMLElement>) xmlList; // Cast safety checked in superclass
		List<ClipboardItem> result = new ArrayList<ClipboardItem>(xmlList.size());
		for (XMLElement element : tXmlList) {
			result.add(createItem(element));
		}
		return result;
	}
	
	private ClipboardItem createItem(XMLElement element) {
		if (element.shortText != null && !element.shortText.trim().isEmpty()) {
			return new TextClipboardItem(element.displayText, element.fileName, element.name, element.shortText);
		} else {
			return new ImageClipboardItem(element.displayText, element.fileName, element.name, element.previewFileName, element.hash);
		}
	}

	@Override
	protected Class<XMLElement> getXmlElementClass() {
		return XMLElement.class;
	}

	private static class XMLElement {
		final String name;
		final String fileName;
		final String displayText;
		final String shortText;
		final String previewFileName;
		final String hash;
		
		public XMLElement(String name, String fileName, String displayText, String shortText, String previewFileName, String hash) {
			this.name = name;
			this.fileName = fileName;
			this.displayText = displayText;
			this.shortText = shortText;
			this.previewFileName = previewFileName;
			this.hash = hash;
		}
	}
}
