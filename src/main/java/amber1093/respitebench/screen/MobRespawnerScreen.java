package amber1093.respitebench.screen;

import amber1093.respitebench.packet.MobRespawnerUpdateC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

//TODO close screen when block is broken
@Environment(EnvType.CLIENT)
public class MobRespawnerScreen extends Screen {

	private static final int SCREEN_WIDTH = 250;
	private static final int SCREEN_HEIGHT = 245;
	private static final int TEXTFIELD_WIDGET_WIDTH = 52;
	private static final int BUTTON_WIDGET_WIDTH = 96;
	private static final int SHORT_TEXT_WIDGET_WIDTH = 96;
	private static final int LONG_TEXT_WIDGET_WIDTH = 150;
	private static final int WIDGET_HEIGHT = 20;
	private static final int WIDGET_SPACING = 25;
	private static final int TEXT_COLOR = 10526880;

	private boolean cancelled = true;

	public BlockPos blockPos;
	public int maxConnectedEntities;
	public int spawnCount;
	public int requiredPlayerRange;
	public int spawnRange;
	public boolean shouldClearEntityData = false;
	public boolean shouldDisconnectEntities = false;
	public boolean active;
	public boolean oneOff;

	public CheckboxWidget activeWidget;
	public CheckboxWidget oneOffWidget;
	public TextFieldWidget maxConnectedEntitiesWidget;
	public TextFieldWidget spawnCountWidget;
	public TextFieldWidget requiredPlayerRangeWidget;
	public TextFieldWidget spawnRangeWidget;

	public MobRespawnerScreen(Text title, BlockPos pos, int maxConnectedEntities, int spawnCount, int requiredPlayerRange, int spawnRange, boolean active, boolean oneOff) {
		super(title);
		this.blockPos = pos;
		this.maxConnectedEntities = maxConnectedEntities;
		this.spawnCount = spawnCount;
		this.requiredPlayerRange = requiredPlayerRange;
		this.spawnRange = spawnRange;
		this.active = active;
		this.oneOff = oneOff;
	}

	@Override
	protected void init() {
		int leftEdge = getLeftEdge(this.width);
		int rightEdge = getRightEdge(this.width);
		int topEdge = getTopEdge(this.height);


		//#region textfield constructors
		this.maxConnectedEntitiesWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 3),
			TEXTFIELD_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.maxconnectedentities")
		);
		this.maxConnectedEntitiesWidget.setText(String.valueOf(this.maxConnectedEntities));

		this.spawnCountWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 4),
			TEXTFIELD_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.spawncount")
		);
		this.spawnCountWidget.setText(String.valueOf(this.spawnCount));

		this.requiredPlayerRangeWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 5),
			TEXTFIELD_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.requiredplayerrange")
		);
		this.requiredPlayerRangeWidget.setText(String.valueOf(this.requiredPlayerRange));

		this.spawnRangeWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 6),
			TEXTFIELD_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.spawnrange")
		);
		this.spawnRangeWidget.setText(String.valueOf(this.spawnRange));
		//#endregion
		
		//#region button constructors
		activeWidget = new CheckboxWidget(
			rightEdge - WIDGET_HEIGHT,
			topEdge + WIDGET_SPACING,
			WIDGET_HEIGHT, WIDGET_HEIGHT,
			ScreenTexts.EMPTY,
			this.active
		);

		oneOffWidget = new CheckboxWidget(
			rightEdge - WIDGET_HEIGHT,
			topEdge + (WIDGET_SPACING * 2),
			WIDGET_HEIGHT, WIDGET_HEIGHT,
			ScreenTexts.EMPTY,
			this.oneOff
		);

		ButtonWidget entityDataClearWidget = (
			ButtonWidget.builder(
				Text.translatable("screen.respitebench.mob_respawner.entitydata.clear"),
				button -> {
					this.shouldClearEntityData = true;
					this.cancelled = false;
					close();
				}
			)
			.dimensions(rightEdge - BUTTON_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 7), BUTTON_WIDGET_WIDTH, WIDGET_HEIGHT)
			.build()
		);

		ButtonWidget connectedEntitiesKillAllWidget = (
			ButtonWidget.builder(
				Text.translatable("screen.respitebench.mob_respawner.connectedentitiesuuid.disconnectall"),
				button -> {
					this.shouldDisconnectEntities = true;
					this.cancelled = false;
					close();
				}
			)
			.dimensions(rightEdge - BUTTON_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 8), BUTTON_WIDGET_WIDTH, WIDGET_HEIGHT)
			.build()
		);

		int finishButtonWidth = (SCREEN_WIDTH / 2) - 4;
		ButtonWidget doneButtonWidget = (
			ButtonWidget.builder(
				ScreenTexts.DONE,
				button -> {
					this.cancelled = false;
					close();
				}
			)
			.dimensions(leftEdge, getBottomEdge(height) - WIDGET_HEIGHT, finishButtonWidth, WIDGET_HEIGHT)
			.build()
		);

		ButtonWidget cancelButtonWidget = (
			ButtonWidget.builder(
				ScreenTexts.CANCEL,
				button -> {
					this.cancelled = true;
					close();
				}
			)
			.dimensions(rightEdge - finishButtonWidth, getBottomEdge(height) - WIDGET_HEIGHT, finishButtonWidth, WIDGET_HEIGHT)
			.build()
		);
		//#endregion


		//title text
		TextWidget titleTextWidget = new TextWidget(
			leftEdge, topEdge,
			SCREEN_WIDTH, WIDGET_HEIGHT,
			title, this.textRenderer
		);

		//add texts
		addDrawable(titleTextWidget);
		addDrawable(getTextWidget(leftEdge, topEdge, LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT, 1, "active", 					false, this.textRenderer));
		addDrawable(getTextWidget(leftEdge, topEdge, LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT, 2, "oneoff", 					false, this.textRenderer));
		addDrawable(getTextWidget(leftEdge, topEdge, LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT, 3, "maxconnectedentities", 	false, this.textRenderer));
		addDrawable(getTextWidget(leftEdge, topEdge, LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT, 4, "spawncount", 				true, this.textRenderer));
		addDrawable(getTextWidget(leftEdge, topEdge, LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT, 5, "requiredplayerrange", 	true, this.textRenderer));
		addDrawable(getTextWidget(leftEdge, topEdge, LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT, 6, "spawnrange", 				true, this.textRenderer));
		addDrawable(getTextWidget(leftEdge, topEdge, SHORT_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT, 7, "entitydata", 			false, this.textRenderer));
		addDrawable(getTextWidget(leftEdge, topEdge, SHORT_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT, 8, "connectedentitiesuuid", 	false, this.textRenderer));

		//add textfields and buttons
		addDrawableChild(this.activeWidget);
		addDrawableChild(this.oneOffWidget);
		addDrawableChild(this.maxConnectedEntitiesWidget);
		addDrawableChild(this.spawnCountWidget);
		addDrawableChild(this.requiredPlayerRangeWidget);
		addDrawableChild(this.spawnRangeWidget);
		addDrawableChild(entityDataClearWidget);
		addDrawableChild(connectedEntitiesKillAllWidget);
		addDrawableChild(doneButtonWidget);
		addDrawableChild(cancelButtonWidget);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public void close() {
		if (!this.cancelled && this.spawnRangeWidget != null) {

			//parse player input then send it to the server
			String maxConnectedEntitiesString = this.maxConnectedEntitiesWidget.getText();
			String spawnCountString = 			this.spawnCountWidget.getText();
			String requiredPlayerRangeString = 	this.requiredPlayerRangeWidget.getText();
			String spawnRangeString = 			this.spawnRangeWidget.getText();

			this.maxConnectedEntities =	(maxConnectedEntitiesString ==	"" ? -1 : Integer.parseInt(maxConnectedEntitiesString	.replaceAll("[\\D]", "")));
			this.spawnCount =			(spawnCountString == 			"" ? -1 : Integer.parseInt(spawnCountString				.replaceAll("[\\D]", "")));
			this.requiredPlayerRange =	(requiredPlayerRangeString ==	"" ? -1 : Integer.parseInt(requiredPlayerRangeString	.replaceAll("[\\D]", "")));
			this.spawnRange =			(spawnRangeString ==			"" ? -1 : Integer.parseInt(spawnRangeString				.replaceAll("[\\D]", "")));

			this.active = activeWidget.isChecked();
			this.oneOff = oneOffWidget.isChecked();

			ClientPlayNetworking.send(new MobRespawnerUpdateC2SPacket(
					this.blockPos,
					this.maxConnectedEntities,
					this.spawnCount,
					this.requiredPlayerRange,
					this.spawnRange,
					this.shouldClearEntityData,
					this.shouldDisconnectEntities,
					this.active,
					this.oneOff
			));
		}
		super.close();
	}

	protected static TextWidget getTextWidget(int x, int y, int width, int height, int index, String key, boolean sameBehaviorAsVanillaSpawner, TextRenderer textRenderer) {
		TextWidget widget = new TextWidget(
			x,
			y + (WIDGET_SPACING * index),
			width, height,
			getTextFromKey(key),
			textRenderer
		);
		widget.alignLeft();
		widget.setTextColor(TEXT_COLOR);
		widget.setTooltip(getTextWidgetTooltip(key, sameBehaviorAsVanillaSpawner));
		return widget;
	}

	protected static Tooltip getTextWidgetTooltip(String key, boolean sameBehaviorAsVanillaSpawner) {
		return Tooltip.of(
			getTextFromKey(key).formatted(Formatting.AQUA)
				.append(ScreenTexts.LINE_BREAK).append(ScreenTexts.LINE_BREAK).append(Text.translatable("screen.respitebench.mob_respawner." + key + ".desc1").formatted(Formatting.WHITE))
				.append(Text.translatable(sameBehaviorAsVanillaSpawner ? "screen.respitebench.mob_respawner.sameasvanillaspawner" : "").formatted(Formatting.GRAY).formatted(Formatting.ITALIC))
				.append(ScreenTexts.LINE_BREAK).append(ScreenTexts.LINE_BREAK).append(Text.translatable("screen.respitebench.mob_respawner." + key + ".desc2").formatted(Formatting.DARK_GRAY))
		);
	}

	protected static MutableText getTextFromKey(String key) {
		return Text.translatable("screen.respitebench.mob_respawner." + key);
	}

	protected static int getLeftEdge(int width) {
		return (width / 2) - (SCREEN_WIDTH / 2);
	}

	protected static int getRightEdge(int width) {
		return (width / 2) + (SCREEN_WIDTH / 2);
	}

	protected static int getTopEdge(int height) {
		return (height / 2) - (SCREEN_HEIGHT / 2);
	}

	protected static int getBottomEdge(int height) {
		return (height / 2) + (SCREEN_HEIGHT / 2);
	}
}
