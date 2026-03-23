package gay.nyako.stickers;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class StickerDisplay {
    private final Component playerName;
    private final Sticker stickerData;
    public UUID playerUUID;
    public float ticks;
    private float lastFrameTime;
    private float currentX;
    private float currentY;
    private float targetX;
    private float targetY;
    private float smoothedDeltaTime;

    public StickerDisplay(Component playerName, Sticker stickerData, UUID playerUUID) {
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

    public void render(GuiGraphicsExtractor guiGraphicsExtractor)
    {
        guiGraphicsExtractor.nextStratum();
        guiGraphicsExtractor.pose().pushMatrix();

        float width = stickerData.width;
        float height = stickerData.height;

        if (currentX == -1) {
            currentX = guiGraphicsExtractor.guiWidth();
        }
        if (currentY == -1) {
            currentY = guiGraphicsExtractor.guiHeight() / 2f - (StickerSystem.STICKER_HEIGHT / 2f);
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

        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, stickerData.identifier, (int) drawX + 1, (int) drawY + 1, 0, 0, drawWidth, drawHeight, drawWidth, drawHeight, ARGB.colorFromFloat(0f, 0f, 0f, 0.5f));
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, stickerData.identifier, (int) drawX, (int) drawY, 0, 0, drawWidth, drawHeight, drawWidth, drawHeight, ARGB.white(1f));

        if (playerName != null) {
            Font textRenderer = Minecraft.getInstance().font;
            guiGraphicsExtractor.text(textRenderer, playerName, (int) (drawX + (StickerSystem.STICKER_WIDTH / 2f) - textRenderer.width(playerName) / 2f), (int) drawY - 8, 0xFFFFFFFF, true);
        }

        guiGraphicsExtractor.pose().popMatrix();
    }

    public void tick() {

    }
}
