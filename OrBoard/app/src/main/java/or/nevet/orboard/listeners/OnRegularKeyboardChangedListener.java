package or.nevet.orboard.listeners;

import or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view.KeyboardLayout;

public interface OnRegularKeyboardChangedListener {
    void onChanged(KeyboardLayout previousLayout);
}
