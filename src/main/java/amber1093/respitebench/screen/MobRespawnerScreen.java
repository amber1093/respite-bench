package amber1093.respitebench.screen;

import amber1093.respitebench.packet.MobRespawnerUpdateC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

//TODO close screen when block is broken
@Environment(EnvType.CLIENT)
public class MobRespawnerScreen extends Screen {

	private static final int SCREEN_WIDTH = 250;
	private static final int SCREEN_HEIGHT = 235;
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
	public boolean active;
	public boolean shouldClearEntityData = false;
	public boolean shouldDisconnectEntities = false;

	public TextFieldWidget maxConnectedEntitiesWidget;
	public TextFieldWidget spawnCountWidget;
	public TextFieldWidget requiredPlayerRangeWidget;
	public TextFieldWidget spawnRangeWidget;
	public CheckboxWidget activeWidget;

	public MobRespawnerScreen(Text title, BlockPos pos, int maxConnectedEntities, int spawnCount, int requiredPlayerRange, int spawnRange, boolean active) {
		super(title);
		this.blockPos = pos;
		this.maxConnectedEntities = maxConnectedEntities;
		this.spawnCount = spawnCount;
		this.requiredPlayerRange = requiredPlayerRange;
		this.spawnRange = spawnRange;
		this.active = active;
	}

	@Override
	protected void init() {
		int leftEdge = getLeftEdge(width);
		int rightEdge = getRightEdge(width);
		int topEdge = getTopEdge(height);

		//#region textfield constructors
		this.maxConnectedEntitiesWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 2),
			TEXTFIELD_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.maxconnectedentities")
		);
		this.maxConnectedEntitiesWidget.setText(String.valueOf(this.maxConnectedEntities));

		this.spawnCountWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 3),
			TEXTFIELD_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.spawncount")
		);
		this.spawnCountWidget.setText(String.valueOf(this.spawnCount));

		this.requiredPlayerRangeWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 4),
			TEXTFIELD_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.requiredplayerrange")
		);
		this.requiredPlayerRangeWidget.setText(String.valueOf(this.requiredPlayerRange));

		this.spawnRangeWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 5),
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

		ButtonWidget entityDataClearWidget = (
			ButtonWidget.builder(
				Text.translatable("screen.respitebench.mob_respawner.entitydata.clear"),
				button -> {
					this.shouldClearEntityData = true;
					close();
				}
			)
			.dimensions(rightEdge - BUTTON_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 6), BUTTON_WIDGET_WIDTH, WIDGET_HEIGHT)
			.build()
		);

		ButtonWidget connectedEntitiesKillAllWidget = (
			ButtonWidget.builder(
				Text.translatable("screen.respitebench.mob_respawner.connectedentitiesuuid.disconnectall"),
				button -> {
					this.shouldDisconnectEntities = true;
					close();
				}
			)
			.dimensions(rightEdge - BUTTON_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 7), BUTTON_WIDGET_WIDTH, WIDGET_HEIGHT)
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

		//#region text constructors
		TextWidget titleTextWidget = new TextWidget(
			leftEdge, topEdge,
			SCREEN_WIDTH, WIDGET_HEIGHT,
			title, this.textRenderer
		);

		TextWidget activeTextWidget = new TextWidget(
			leftEdge, topEdge + WIDGET_SPACING,
			LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.active"),
			this.textRenderer
		);

		TextWidget maxConnectedEntitiesTextWidget = new TextWidget(
			leftEdge, topEdge + (WIDGET_SPACING * 2),
			LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.maxconnectedentities"),
			this.textRenderer
		);

		TextWidget spawnCountTextWidget = new TextWidget(
			leftEdge, topEdge + (WIDGET_SPACING * 3),
			LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.spawncount"),
			this.textRenderer
		);

		TextWidget requiredPlayerRangeTextWidget = new TextWidget(
			leftEdge, topEdge + (WIDGET_SPACING * 4),
			LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.requiredplayerrange"),
			this.textRenderer
		);

		TextWidget spawnRangeTextWidget = new TextWidget(
			leftEdge, topEdge + (WIDGET_SPACING * 5),
			LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.spawnrange"),
			this.textRenderer
		);

		TextWidget entityDataTextWidget = new TextWidget(
			leftEdge, topEdge + (WIDGET_SPACING * 6),
			SHORT_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.entitydata"),
			this.textRenderer
		);

		TextWidget connectedEntitiesTextWidget = new TextWidget(
			leftEdge, topEdge + (WIDGET_SPACING * 7),
			SHORT_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.translatable("screen.respitebench.mob_respawner.connectedentitiesuuid"),
			this.textRenderer
		);
		//#endregion

		//#region text settings

		//set alignments
		titleTextWidget					.alignCenter();
		activeTextWidget				.alignLeft();
		maxConnectedEntitiesTextWidget	.alignLeft();
		spawnCountTextWidget			.alignLeft();
		requiredPlayerRangeTextWidget	.alignLeft();
		spawnRangeTextWidget			.alignLeft();
		entityDataTextWidget			.alignLeft();
		connectedEntitiesTextWidget		.alignLeft();

		//set tooltips
		activeTextWidget				.setTooltip(getTextWidgetTooltip("active", false));
		maxConnectedEntitiesTextWidget	.setTooltip(getTextWidgetTooltip("maxconnectedentities", false));
		spawnCountTextWidget			.setTooltip(getTextWidgetTooltip("spawncount", true));	
		requiredPlayerRangeTextWidget	.setTooltip(getTextWidgetTooltip("requiredplayerrange", true));	
		spawnRangeTextWidget			.setTooltip(getTextWidgetTooltip("spawnrange", true));	
		entityDataTextWidget			.setTooltip(getTextWidgetTooltip("entitydata", false));	
		connectedEntitiesTextWidget		.setTooltip(getTextWidgetTooltip("connectedentitiesuuid", false));

		//set text color
		activeTextWidget				.setTextColor(TEXT_COLOR);
		maxConnectedEntitiesTextWidget	.setTextColor(TEXT_COLOR);
		spawnCountTextWidget			.setTextColor(TEXT_COLOR);
		requiredPlayerRangeTextWidget	.setTextColor(TEXT_COLOR);
		spawnRangeTextWidget			.setTextColor(TEXT_COLOR);
		entityDataTextWidget			.setTextColor(TEXT_COLOR);
		connectedEntitiesTextWidget		.setTextColor(TEXT_COLOR);
		//#endregion

		//add texts
		addDrawable(titleTextWidget);
		addDrawable(activeTextWidget);
		addDrawable(maxConnectedEntitiesTextWidget);
		addDrawable(spawnCountTextWidget);
		addDrawable(requiredPlayerRangeTextWidget);
		addDrawable(spawnRangeTextWidget);
		addDrawable(entityDataTextWidget);
		addDrawable(connectedEntitiesTextWidget);

		//add textfields and buttons
		addDrawableChild(this.activeWidget);
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
			String spawnCountString = this.spawnCountWidget.getText();
			String requiredPlayerRangeString = this.requiredPlayerRangeWidget.getText();
			String spawnRangeString = this.spawnRangeWidget.getText();

			this.maxConnectedEntities = (maxConnectedEntitiesString == "" ? -1 : Integer.parseInt(maxConnectedEntitiesString.replaceAll("[\\D]", "")));
			this.spawnCount = (spawnCountString == "" ? -1 : Integer.parseInt(spawnCountString.replaceAll("[\\D]", "")));
			this.requiredPlayerRange = (requiredPlayerRangeString == "" ? -1 : Integer.parseInt(requiredPlayerRangeString.replaceAll("[\\D]", "")));
			this.spawnRange = (spawnRangeString == "" ? -1 : Integer.parseInt(spawnRangeString.replaceAll("[\\D]", "")));

			this.active = activeWidget.isChecked();

			ClientPlayNetworking.send(new MobRespawnerUpdateC2SPacket(
					this.blockPos,
					this.maxConnectedEntities,
					this.spawnCount,
					this.requiredPlayerRange,
					this.spawnRange,
					this.shouldClearEntityData,
					this.shouldDisconnectEntities,
					this.active
			));
		}
		super.close();
	}

	protected static Tooltip getTextWidgetTooltip(String key, boolean sameBehaviorAsVanillaSpawner) {
		return Tooltip.of(
			Text.translatable("screen.respitebench.mob_respawner." + key).formatted(Formatting.AQUA)
			.append(ScreenTexts.LINE_BREAK).append(ScreenTexts.LINE_BREAK).append(Text.translatable("screen.respitebench.mob_respawner." + key + ".desc1").formatted(Formatting.WHITE))
			.append(Text.translatable(sameBehaviorAsVanillaSpawner ? "screen.respitebench.mob_respawner.sameasvanillaspawner" : "").formatted(Formatting.GRAY).formatted(Formatting.ITALIC))
			.append(ScreenTexts.LINE_BREAK).append(ScreenTexts.LINE_BREAK).append(Text.translatable("screen.respitebench.mob_respawner." + key + ".desc2").formatted(Formatting.DARK_GRAY))
		);
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
