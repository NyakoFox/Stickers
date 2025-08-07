package gay.nyako.stickers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;

public class StickerDisplay {
    private final Text playerName;
    private final Sticker stickerData;
    public UUID playerUUID;
    public float ticks;
    private float lastFrameTime;
    private float currentX;
    private float currentY;
    private float targetX;
    private float targetY;
    private float smoothedDeltaTime;

    public StickerDisplay(Text playerName, Sticker stickerData, UUID playerUUID) {
        this.playerName = playerName;
        this.stickerData = stickerData;
        this.playerUUID = playerUUID;

        lastFrameTime = System.nanoTime();
        ticks = 0;
        currentX = -1;
        currentY = -1;
        targetX = -1;
        targetY = -1;

        smoothedDeltaTime = 0.05f; // default to 20 TPS
    }

    public void setTarget(float x, float y) {
        targetX = x;
        targetY = y;
    }

    public void render(DrawContext drawContext, float tickDelta)
    {
        float width = stickerData.width;
        float height = stickerData.height;

        if (currentX == -1) {
            currentX = drawContext.getScaledWindowWidth();
        }
        if (currentY == -1) {
            currentY = drawContext.getScaledWindowHeight() / 2f - (StickerSystem.STICKER_HEIGHT / 2f);
        }

        float smoothing = 8f;

        long now = System.nanoTime();

        float rawDelta = (now - lastFrameTime) / 1_000_000_000f;
        lastFrameTime = now;

        rawDelta = Math.max(0f, Math.min(rawDelta, 1f / 5f));
        smoothedDeltaTime = smoothedDeltaTime * 0.9f + rawDelta * 0.1f;

        currentX += (targetX - currentX) * smoothing * smoothedDeltaTime;
        currentY += (targetY - currentY) * smoothing * smoothedDeltaTime;

        // ok, we have the source width and height...
        // we need to grab the aspect ratio.
        // stickers are always drawn at STICKER_WIDTH and STICKER_HEIGHT
        // so we need to scale the sticker up/down to fit that...
        // and also offset drawX and drawY to center the actual sticker within STICKER_WIDTH and STICKER_HEIGHT

        float aspectRatio = width / height;
        int drawWidth;
        int drawHeight;
        if (aspectRatio > 1) {
            // wider than tall
            drawWidth = StickerSystem.STICKER_WIDTH;
            drawHeight = (int) (StickerSystem.STICKER_WIDTH / aspectRatio);
        } else {
            // taller than wide
            drawHeight = StickerSystem.STICKER_HEIGHT;
            drawWidth = (int) (StickerSystem.STICKER_HEIGHT * aspectRatio);
        }

        float drawX = currentX + (StickerSystem.STICKER_WIDTH - drawWidth) / 2f;
        float drawY = currentY + (StickerSystem.STICKER_HEIGHT - drawHeight) / 2f;

        drawContext.setShaderColor(0, 0, 0, 0.5f);
        drawContext.drawTexture(stickerData.identifier, (int) drawX + 1, (int) drawY + 1, 0, 0, drawWidth, drawHeight, drawWidth, drawHeight);
        drawContext.setShaderColor(1, 1, 1, 1);
        drawContext.drawTexture(stickerData.identifier, (int) drawX, (int) drawY, 0, 0, drawWidth, drawHeight, drawWidth, drawHeight);

        if (playerName != null) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            drawContext.drawText(textRenderer, playerName, (int) (drawX + (StickerSystem.STICKER_WIDTH / 2f) - textRenderer.getWidth(playerName) / 2f), (int) drawY - 8, 0xFFFFFFFF, true);
        }
    }

    public void tick() {

    }
}
