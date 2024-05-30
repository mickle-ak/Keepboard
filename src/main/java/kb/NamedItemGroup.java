package kb;

public class NamedItemGroup {
	private final String name;
	private final ItemGroup itemGroup;
	
	public NamedItemGroup(String name, String itemGroupFileName) {
		this.name = name;
		this.itemGroup = new ItemGroup(itemGroupFileName);
	}

	public NamedItemGroup(String name, ItemGroup itemGroup) {
		this.name = name;
		this.itemGroup = itemGroup;
	}

	public String getName() {
		return name;
	}

	public ItemGroup getItemGroup() {
		return itemGroup;
	}

	@Override
	public String toString() {
		return name;
	}
}
