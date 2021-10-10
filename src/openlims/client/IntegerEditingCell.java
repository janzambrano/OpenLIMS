/**
 * Class for creating editable quantity cell for an item table
 */

package openlims.client;

import java.util.regex.Pattern;

import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;

public class IntegerEditingCell extends TableCell<Item, Integer> {

    private final TextField textField = new TextField();
    private final Pattern intPattern = Pattern.compile("-?\\d+");

    public IntegerEditingCell() {
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (! isNowFocused) {
                processEdit();
            }
        });
        textField.setOnAction(event -> processEdit());
    }

    private void processEdit() {
        String text = textField.getText();
        if (intPattern.matcher(text).matches()) {
            commitEdit(Integer.parseInt(text));
        } else {
            cancelEdit();
        }
    }

    @Override
    public void updateItem(Integer value, boolean empty) {
        super.updateItem(value, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (isEditing()) {
            setText(null);
            textField.setText(value.toString());
            setGraphic(textField);
        } else {
            setText(value.toString());
            setGraphic(null);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        Number value = getItem();
        if (value != null) {
            textField.setText(value.toString());
            setGraphic(textField);
            setText(null);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem().toString());
        setGraphic(null);
    }

    // This seems necessary to persist the edit on loss of focus; not sure why:
    @Override
    public void commitEdit(Integer value) {
        super.commitEdit(value);
        ((Item)this.getTableRow().getItem()).setCount(value.intValue());
    }
}
