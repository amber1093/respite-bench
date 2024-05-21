package amber1093.respitebench.screen;

import org.joml.Vector4i;

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
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
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
	private static final int TEXTFIELD_WIDTH = 52;
	private static final int BUTTON_WIDTH = 96;
	private static final int WIDE_BUTTON_WIDTH = (SCREEN_WIDTH / 2) - 4;
	private static final int SHORT_TEXT_WIDTH = 96;
	private static final int LONG_TEXT_WIDTH = 150;
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
	public boolean enabled;
	public boolean oneOff;

	public CheckboxWidget enabledWidget;
	public CheckboxWidget oneOffWidget;
	public TextFieldWidget maxConnectedEntitiesWidget;
	public TextFieldWidget spawnCountWidget;
	public TextFieldWidget requiredPlayerRangeWidget;
	public TextFieldWidget spawnRangeWidget;

	public MobRespawnerScreen(Text title, BlockPos pos, int maxConnectedEntities, int spawnCount, int requiredPlayerRange, int spawnRange, boolean enabled, boolean oneOff) {
		super(title);
		this.blockPos = pos;
		this.maxConnectedEntities = maxConnectedEntities;
		this.spawnCount = spawnCount;
		this.requiredPlayerRange = requiredPlayerRange;
		this.spawnRange = spawnRange;
		this.enabled = enabled;
		this.oneOff = oneOff;
	}

	@Override
	protected void init() {
		int leftEdge = getLeftEdge(this.width);
		int rightEdge = getRightEdge(this.width);
		int topEdge = getTopEdge(this.height);
		int bottomEdge = getBottomEdge(this.height);


		//add title
		addDrawable(new TextWidget(leftEdge, topEdge, SCREEN_WIDTH, WIDGET_HEIGHT, title, this.textRenderer));


		//#region add text
		Vector4i longTextTransform =	new Vector4i(leftEdge, topEdge, LONG_TEXT_WIDTH, WIDGET_HEIGHT);
		Vector4i shortTextTransform =	new Vector4i(leftEdge, topEdge, SHORT_TEXT_WIDTH, WIDGET_HEIGHT);

		addDrawable(getTextWidget(longTextTransform, 1, "enabled",					false, false, this.textRenderer));
		addDrawable(getTextWidget(longTextTransform, 2, "oneoff",					false, false, this.textRenderer));
		addDrawable(getTextWidget(longTextTransform, 3, "maxconnectedentities",		false, true, this.textRenderer));
		addDrawable(getTextWidget(longTextTransform, 4, "spawncount",				true, true, this.textRenderer));
		addDrawable(getTextWidget(longTextTransform, 5, "requiredplayerrange",		true, false, this.textRenderer));
		addDrawable(getTextWidget(longTextTransform, 6, "spawnrange",				true, false, this.textRenderer));
		addDrawable(getTextWidget(shortTextTransform, 7, "entitydata",				false, false, this.textRenderer));
		addDrawable(getTextWidget(shortTextTransform, 8, "connectedentitiesuuid",	false, false, this.textRenderer));
		//#endregion


		//#region add checkboxes
		Vector4i checkboxTransform = new Vector4i(rightEdge - WIDGET_HEIGHT, topEdge, WIDGET_HEIGHT, WIDGET_HEIGHT);

		this.enabledWidget = getCheckboxWidget(checkboxTransform, 1, this.enabled);
		this.oneOffWidget = getCheckboxWidget(checkboxTransform, 2, this.oneOff);

		addDrawableChild(this.enabledWidget);
		addDrawableChild(this.oneOffWidget);
		//#endregion


		//#region add textfields
		Vector4i textFieldTransform =	new Vector4i(rightEdge - TEXTFIELD_WIDTH, topEdge, TEXTFIELD_WIDTH, WIDGET_HEIGHT);

		this.maxConnectedEntitiesWidget =	getTextFieldWidget(textFieldTransform, 3, this.maxConnectedEntities, "maxconnectedentities", this.textRenderer);
		this.spawnCountWidget =				getTextFieldWidget(textFieldTransform, 4, this.spawnCount, "spawncount", this.textRenderer);
		this.requiredPlayerRangeWidget =	getTextFieldWidget(textFieldTransform, 5, this.requiredPlayerRange, "requiredplayerrange", this.textRenderer);
		this.spawnRangeWidget =				getTextFieldWidget(textFieldTransform, 6, this.spawnRange, "spawnrange", this.textRenderer);

		addDrawableChild(this.maxConnectedEntitiesWidget);
		addDrawableChild(this.spawnCountWidget);
		addDrawableChild(this.requiredPlayerRangeWidget);
		addDrawableChild(this.spawnRangeWidget);
		//#endregion


		//#region add buttons
		Vector4i buttonTransform = new Vector4i(rightEdge - BUTTON_WIDTH, topEdge, BUTTON_WIDTH, WIDGET_HEIGHT);

		addDrawableChild(getButtonWidget(buttonTransform, 7, "entitydata.clear", button -> {
			this.shouldClearEntityData = true;
			this.cancelled = false;
			close();
		}));

		addDrawableChild(getButtonWidget(buttonTransform, 8, "connectedentitiesuuid.disconnectall", button -> {
			this.shouldDisconnectEntities = true;
			this.cancelled = false;
			close();
		}));

		Vector4i doneButtonTransform = new Vector4i(leftEdge, bottomEdge - WIDGET_HEIGHT, WIDE_BUTTON_WIDTH, WIDGET_HEIGHT);
		Vector4i cancelButtonTransform = new Vector4i(rightEdge - WIDE_BUTTON_WIDTH, bottomEdge - WIDGET_HEIGHT, WIDE_BUTTON_WIDTH, WIDGET_HEIGHT);

		addDrawableChild(getButtonWidget(doneButtonTransform, 0, ScreenTexts.DONE, button -> {
			this.cancelled = false;
			close();
		}));

		addDrawableChild(getButtonWidget(cancelButtonTransform, 0, ScreenTexts.CANCEL, button -> {
			this.cancelled = true;
			close();
		}));
		//#endregion
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

			this.enabled = enabledWidget.isChecked();
			this.oneOff = oneOffWidget.isChecked();

			ClientPlayNetworking.send(new MobRespawnerUpdateC2SPacket(
					this.blockPos,
					this.maxConnectedEntities,
					this.spawnCount,
					this.requiredPlayerRange,
					this.spawnRange,
					this.shouldClearEntityData,
					this.shouldDisconnectEntities,
					this.enabled,
					this.oneOff
			));
		}
		super.close();
	}

	protected static ButtonWidget getButtonWidget(Vector4i transform, int row, String key, PressAction action) {
		return getButtonWidget(transform, row, getTextFromKey(key), action);
	}

	protected static ButtonWidget getButtonWidget(Vector4i transform, int row, Text text, PressAction action) {
		return ButtonWidget
		.builder(text, action)
		.dimensions(
			transform.x(),
			transform.y() + (WIDGET_SPACING * row),
			transform.z(),
			transform.w()
		)
		.build();
	}

	protected static CheckboxWidget getCheckboxWidget(Vector4i transform, int row, boolean value) {
		return new CheckboxWidget(
			transform.x(),
			transform.y() + (WIDGET_SPACING * row),
			transform.z(),
			transform.w(),
			ScreenTexts.EMPTY,
			value
		);
	}

	protected static TextFieldWidget getTextFieldWidget(Vector4i transform, int row, int value, String key, TextRenderer textRenderer) {
		return getTextFieldWidget(transform, row, value, getTextFromKey(key), textRenderer);
	}

	protected static TextFieldWidget getTextFieldWidget(Vector4i transform, int row, int value, Text text, TextRenderer textRenderer) {
		TextFieldWidget widget = new TextFieldWidget(
			textRenderer,
			transform.x(),
			transform.y() + (WIDGET_SPACING * row),
			transform.z(),
			transform.w(),
			text
		);
		widget.setText(String.valueOf(value));
		return widget;
	}

	protected static TextWidget getTextWidget(Vector4i transform, int row, String key, boolean sameAsVanilla, boolean lagWarning, TextRenderer textRenderer) {
		return getTextWidget(transform, row, getTextFromKey(key), getTooltip(key, sameAsVanilla, lagWarning), textRenderer);
	}

	protected static TextWidget getTextWidget(Vector4i transform, int row, Text text, Tooltip tooltip, TextRenderer textRenderer) {
		TextWidget widget = new TextWidget(
			transform.x(),
			transform.y() + (WIDGET_SPACING * row),
			transform.z(),
			transform.w(),
			text,
			textRenderer
		);
		widget.alignLeft();
		widget.setTextColor(TEXT_COLOR);
		widget.setTooltip(tooltip);
		return widget;
	}

	protected static Tooltip getTooltip(String key, boolean sameAsVanilla, boolean lagWarning) {
		return Tooltip.of(
			getTextFromKey(key).formatted(Formatting.AQUA)
				.append(Text.literal("\n\n")).append(getTextFromKey(key + ".description")			.formatted(Formatting.WHITE))
				.append((sameAsVanilla ? getTextFromKey("sameasvanillaspawner") : Text.empty())	.formatted(Formatting.GRAY).formatted(Formatting.ITALIC))
				.append((lagWarning ? getTextFromKey("lagwarning") : Text.empty())				.formatted(Formatting.YELLOW))
				.append(Text.literal("\n\n")).append(getTextFromKey(key + ".nbtinfo")				.formatted(Formatting.DARK_GRAY))
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
