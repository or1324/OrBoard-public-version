package or.nevet.orboard.keyboard_general_operation.related_to_main_keyboard_view;

public class UpperCaseManager {
    private enum State {
        SHIFT, CAPSLOCK, REGULAR
    }

    private State currentState = State.REGULAR;

    public void onUpperCaseButtonClicked() {
        switch (currentState) {
            case CAPSLOCK:
                currentState = State.REGULAR;
                break;
            case SHIFT:
                currentState = State.CAPSLOCK;
                break;
            case REGULAR:
                currentState = State.SHIFT;
                break;
        }
    }

    public void onRegularKeyClicked() {
        if (currentState == State.SHIFT)
            currentState = State.REGULAR;
    }

    public boolean isCapsLock() {
        return currentState == State.CAPSLOCK;
    }

    public boolean shouldBeUpperCase() {
        return currentState == State.CAPSLOCK || currentState == State.SHIFT;
    }
}
