package kb;

public class SelectedClipboardContent {
    private final ClipboardContent clipboardContent;
    private final boolean onlyCopyToClipboard;

    public SelectedClipboardContent(ClipboardContent clipboardContent, boolean onlyCopyToClipboard) {
        this.clipboardContent = clipboardContent;
        this.onlyCopyToClipboard = onlyCopyToClipboard;
    }

    public ClipboardContent getClipboardContent() {
        return clipboardContent;
    }

    public boolean isOnlyCopyToClipboard() {
        return onlyCopyToClipboard;
    }
}
