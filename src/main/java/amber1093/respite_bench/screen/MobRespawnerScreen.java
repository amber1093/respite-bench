package amber1093.respite_bench.screen;

import amber1093.respite_bench.RespiteBench;
import amber1093.respite_bench.packet.MobRespawnerUpdateC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class MobRespawnerScreen extends Screen {

	private static final Identifier TEXTURE = new Identifier(RespiteBench.MOD_ID, "textures/gui/mob_respawner.png");
	private static final int BACKGROUND_WIDTH = 224;
	private static final int BACKGROUND_HEIGHT = 174;
	private static final int TEXTFIELD_WIDGET_WIDTH = 26;
	private static final int BUTTON_WIDGET_WIDTH = 96;
	private static final int SHORT_TEXT_WIDGET_WIDTH = 96;
	private static final int LONG_TEXT_WIDGET_WIDTH = 150;
	private static final int WIDGET_HEIGHT = 16;
	private static final int EDGE_SPACING = 8;
	private static final int WIDGET_SPACING = 19;

	public BlockPos blockPos;
	public int maxConnectedEntities;
	public int spawnCount;
	public int requiredPlayerRange;
	public int spawnRange;
	public boolean shouldClearEntityData = false;
	public boolean shouldDisconnectEntities = false;

	public TextFieldWidget maxConnectedEntitiesWidget;
	public TextFieldWidget spawnCountWidget;
	public TextFieldWidget requiredPlayerRangeWidget;
	public TextFieldWidget spawnRangeWidget;

	public MobRespawnerScreen(Text title, BlockPos pos, int maxConnectedEntities, int spawnCount, int requiredPlayerRange, int spawnRange) {
		super(title);
		this.blockPos = pos;
		this.maxConnectedEntities = maxConnectedEntities;
		this.spawnCount = spawnCount;
		this.requiredPlayerRange = requiredPlayerRange;
		this.spawnRange = spawnRange;
	}

	@Override
	protected void init() {
		int leftEdge = getLeftEdge(width);
		int rightEdge = getRightEdge(width);
		int topEdge = getTopEdge(height);
		

		//#region textfield constructors
		maxConnectedEntitiesWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + WIDGET_SPACING,
			TEXTFIELD_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.literal(String.valueOf(this.maxConnectedEntities))
		);

		spawnCountWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 2),
			TEXTFIELD_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.literal(String.valueOf(this.spawnCount))
		);

		requiredPlayerRangeWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 3),
			TEXTFIELD_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.literal(String.valueOf(this.requiredPlayerRange))
		);

		spawnRangeWidget = new TextFieldWidget(
			this.textRenderer,
			rightEdge - TEXTFIELD_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 4),
			TEXTFIELD_WIDGET_WIDTH, WIDGET_HEIGHT,
			Text.literal(String.valueOf(this.spawnRange))
		);
		//#endregion

		//#region button constructors
		ButtonWidget entityDataClearWidget = (
			ButtonWidget.builder(
				Text.translatable("screen.respite_bench.mob_respawner.entitydata.clear"),
				button -> {
					this.shouldClearEntityData = true;
					close();
				}
			)
			.dimensions(rightEdge - BUTTON_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 5), BUTTON_WIDGET_WIDTH, WIDGET_HEIGHT)
			.build()
		);

		ButtonWidget connectedEntitiesKillAllWidget = (
			ButtonWidget.builder(
				Text.translatable("screen.respite_bench.mob_respawner.connectedentitiesuuid.disconnectall"),
				button -> {
					this.shouldDisconnectEntities = true;
					close();
				}
			)
			.dimensions(rightEdge - BUTTON_WIDGET_WIDTH, topEdge + (WIDGET_SPACING * 6), BUTTON_WIDGET_WIDTH, WIDGET_HEIGHT)
			.build()
		);

		ButtonWidget doneButtonWidget = (
			ButtonWidget.builder(
				ScreenTexts.DONE,
				button -> {
					close();
				}
			)
			.dimensions((width / 2) - (BUTTON_WIDGET_WIDTH / 2), getBottomEdge(height) - WIDGET_HEIGHT, BUTTON_WIDGET_WIDTH, WIDGET_HEIGHT)
			.build()
		);
		//#endregion

		//#region text constructors
		TextWidget maxConnectedEntitiesTextWidget = new TextWidget(
			leftEdge, topEdge + WIDGET_SPACING,
			LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			ScreenTexts.EMPTY, this.textRenderer
		);
		maxConnectedEntitiesTextWidget.alignLeft();
		maxConnectedEntitiesTextWidget.setTooltip(getTextWidgetTooltip("maxconnectedentities", false));	
		
		TextWidget spawnCountTextWidget = new TextWidget(
			leftEdge, topEdge + (WIDGET_SPACING * 2),
			LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			ScreenTexts.EMPTY, this.textRenderer
		);
		spawnCountTextWidget.alignLeft();
		spawnCountTextWidget.setTooltip(getTextWidgetTooltip("spawncount", true));	
		
		TextWidget requiredPlayerRangeTextWidget = new TextWidget(
			leftEdge, topEdge + (WIDGET_SPACING * 3),
			LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			ScreenTexts.EMPTY, this.textRenderer
		);
		requiredPlayerRangeTextWidget.alignLeft();
		requiredPlayerRangeTextWidget.setTooltip(getTextWidgetTooltip("requiredplayerrange", true));	
		
		TextWidget spawnRangeTextWidget = new TextWidget(
			leftEdge, topEdge + (WIDGET_SPACING * 4),
			LONG_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			ScreenTexts.EMPTY, this.textRenderer
		);
		spawnRangeTextWidget.alignLeft();
		spawnRangeTextWidget.setTooltip(getTextWidgetTooltip("spawnrange", true));	

		TextWidget entityDataTextWidget = new TextWidget(
			leftEdge, topEdge + (WIDGET_SPACING * 5),
			SHORT_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			ScreenTexts.EMPTY, this.textRenderer
		);
		entityDataTextWidget.alignLeft();
		entityDataTextWidget.setTooltip(getTextWidgetTooltip("entitydata", false));	

		TextWidget connectedEntitiesTextWidget = new TextWidget(
			leftEdge, topEdge + (WIDGET_SPACING * 6),
			SHORT_TEXT_WIDGET_WIDTH, WIDGET_HEIGHT,
			ScreenTexts.EMPTY, this.textRenderer
		);
		connectedEntitiesTextWidget.alignLeft();
		connectedEntitiesTextWidget.setTooltip(getTextWidgetTooltip("connectedentitiesuuid", false));	
		//#endregion


		//add texts
		addDrawable(maxConnectedEntitiesTextWidget);
		addDrawable(spawnCountTextWidget);
		addDrawable(requiredPlayerRangeTextWidget);
		addDrawable(spawnRangeTextWidget);
		addDrawable(entityDataTextWidget);
		addDrawable(connectedEntitiesTextWidget);

		//add textfields
		addDrawableChild(maxConnectedEntitiesWidget);
		addDrawableChild(spawnCountWidget);
		addDrawableChild(requiredPlayerRangeWidget);
		addDrawableChild(spawnRangeWidget);

		//add buttons
		addDrawableChild(entityDataClearWidget);
		addDrawableChild(connectedEntitiesKillAllWidget);
		addDrawableChild(doneButtonWidget);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);

		//draw background texture
		int leftEdge = getLeftEdge(width);
		int topEdge = getTopEdge(height);
        context.drawTexture(TEXTURE, leftEdge - EDGE_SPACING, topEdge - EDGE_SPACING, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

		//draw all texts
		context.drawText(this.textRenderer, title, getLeftEdge(width), getTopEdge(height), 4144959, false);
		context.drawText(this.textRenderer, Text.translatable("screen.respite_bench.mob_respawner.maxconnectedentities"), leftEdge, topEdge + WIDGET_SPACING + 4, 4144959, false);
		context.drawText(this.textRenderer, Text.translatable("screen.respite_bench.mob_respawner.spawncount"), leftEdge, topEdge + (WIDGET_SPACING * 2) + 4, 4144959, false);
		context.drawText(this.textRenderer, Text.translatable("screen.respite_bench.mob_respawner.requiredplayerrange"), leftEdge, topEdge + (WIDGET_SPACING * 3) + 4, 4144959, false);
		context.drawText(this.textRenderer, Text.translatable("screen.respite_bench.mob_respawner.spawnrange"), leftEdge, topEdge + (WIDGET_SPACING * 4) + 4, 4144959, false);
		context.drawText(this.textRenderer, Text.translatable("screen.respite_bench.mob_respawner.entitydata"), leftEdge, topEdge + (WIDGET_SPACING * 5) + 4, 4144959, false);
		context.drawText(this.textRenderer, Text.translatable("screen.respite_bench.mob_respawner.connectedentitiesuuid"), leftEdge, topEdge + (WIDGET_SPACING * 6) + 4, 4144959, false);

		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public void close() {
		if (spawnRangeWidget != null) {

			String maxConnectedEntitiesString = maxConnectedEntitiesWidget.getText();
			String spawnCountString = spawnCountWidget.getText();
			String requiredPlayerRangeString = requiredPlayerRangeWidget.getText();
			String spawnRangeString = spawnRangeWidget.getText();
			
			int maxConnectedEntities = (maxConnectedEntitiesString == "" ? -1 : Integer.parseInt(maxConnectedEntitiesString.replaceAll("[\\D]", "")));
			int spawnCount = (spawnCountString == "" ? -1 : Integer.parseInt(spawnCountString.replaceAll("[\\D]", "")));
			int requiredPlayerRange = (requiredPlayerRangeString == "" ? -1 : Integer.parseInt(requiredPlayerRangeString.replaceAll("[\\D]", "")));
			int spawnRange = (spawnRangeString == "" ? -1 : Integer.parseInt(spawnRangeString.replaceAll("[\\D]", "")));

			ClientPlayNetworking.send(new MobRespawnerUpdateC2SPacket(
					this.blockPos,
					maxConnectedEntities,
					spawnCount,
					requiredPlayerRange,
					spawnRange,
					this.shouldClearEntityData,
					this.shouldDisconnectEntities
			));

		}
		super.close();
	}

	protected static Tooltip getTextWidgetTooltip(String key, boolean sameBehaviourAsVanillaSpawner) {
		return Tooltip.of(
			Text.translatable("screen.respite_bench.mob_respawner." + key).formatted(Formatting.AQUA)
			.append(ScreenTexts.LINE_BREAK).append(ScreenTexts.LINE_BREAK).append(Text.translatable("screen.respite_bench.mob_respawner." + key + ".desc1").formatted(Formatting.WHITE))
			.append(Text.translatable(sameBehaviourAsVanillaSpawner ? "screen.respite_bench.mob_respawner.sameasvanillaspawner" : "").formatted(Formatting.GRAY).formatted(Formatting.ITALIC))
			.append(ScreenTexts.LINE_BREAK).append(ScreenTexts.LINE_BREAK).append(Text.translatable("screen.respite_bench.mob_respawner." + key + ".desc2").formatted(Formatting.DARK_GRAY))
		);
	}

	protected static int getLeftEdge(int width) {
		return (width / 2) - (BACKGROUND_WIDTH / 2) + EDGE_SPACING;
	}

	protected static int getRightEdge(int width) {
		return (width / 2) + (BACKGROUND_WIDTH / 2) - EDGE_SPACING;
	}

	protected static int getTopEdge(int height) {
		return (height / 2) - (BACKGROUND_HEIGHT / 2) + EDGE_SPACING;
	}

	protected static int getBottomEdge(int height) {
		return (height / 2) + (BACKGROUND_HEIGHT / 2) - EDGE_SPACING;
	}
}
