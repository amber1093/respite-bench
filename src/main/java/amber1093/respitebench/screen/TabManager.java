package amber1093.respitebench.screen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.widget.ButtonWidget;

public class TabManager {

	private List<ButtonWidget> buttonWidgets = new ArrayList<>();
	private int currentTab = 0;
	public TabManager() {}

	public void addTab(ButtonWidget button) {
		if (this.buttonWidgets.size() == this.getCurrentTab()) {
			button.active = false;
		}
		this.buttonWidgets.add(button);
	}

	public ButtonWidget getTab(int index) {
		return this.buttonWidgets.get(index);
	}

	public void setCurrentTab(int index) {
		this.currentTab = index;
	}

	public int getCurrentTab() {
		return this.currentTab;
	}

	public void clear() {
		this.buttonWidgets.clear();
	}

	public void enableAllTabs() {
		this.buttonWidgets.forEach((button) -> {
			button.active = true;
		});
	}
}
