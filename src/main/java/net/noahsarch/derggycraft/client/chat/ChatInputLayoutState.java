package net.noahsarch.derggycraft.client.chat;

public final class ChatInputLayoutState {
    private static int chatOffset;

    private ChatInputLayoutState() {
    }

    public static int getChatOffset() {
        return chatOffset;
    }

    public static void setChatOffset(int offset) {
        chatOffset = Math.max(offset, 0);
    }

    public static void reset() {
        chatOffset = 0;
    }
}