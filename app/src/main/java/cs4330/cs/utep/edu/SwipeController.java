package cs4330.cs.utep.edu;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public class SwipeController extends ItemTouchHelper.SimpleCallback {

    private ItemAdapter itemAdapter;

    private final ColorDrawable editBackground;
    private Drawable editIcon;
    private final ColorDrawable deleteBackground;
    private Drawable deleteIcon;

    /**
     * Initialize swipe controller for left and right
     * @param itemAdapter
     */
    public SwipeController(ItemAdapter itemAdapter){
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.itemAdapter = itemAdapter;

        editIcon = ContextCompat.getDrawable(itemAdapter.getContext(),
                R.drawable.ic_edit_white_36dp);
        editBackground = new ColorDrawable(Color.GREEN);

        deleteIcon = ContextCompat.getDrawable(itemAdapter.getContext(),
                R.drawable.ic_delete_white_36dp);
        deleteBackground = new ColorDrawable(Color.RED);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    /**
     * Called when an item is swiped of the screen
     * @param viewHolder - Row view that was swiped
     * @param direction  - Direction of the swipe
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT)
            itemAdapter.deleteItem(pos);
        else {
            itemAdapter.updateItem(pos);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isActive){
        super.onChildDraw(c, recyclerView, viewHolder, dX,
                dY, actionState, isActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 15;

        int iconMargin = (itemView.getHeight() - editIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - editIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + editIcon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = iconLeft + editIcon.getIntrinsicWidth();
            editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            editBackground.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());

            editBackground.draw(c);
            editIcon.draw(c);
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            deleteBackground.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());

            deleteBackground.draw(c);
            deleteIcon.draw(c);
        } else { // view is unSwiped
            deleteBackground.setBounds(0, 0, 0, 0);
            editBackground.setBounds(0, 0, 0, 0);
        }


    }

}
