package ramdan.file.line.token.callback;

import ramdan.file.line.token.callback.Callback;

import java.util.ArrayList;

public class ArrayListCallback<T> implements Callback<T> {

    private ArrayList<T> arrayList;

    public ArrayList<T> getArrayList() {
        return arrayList;
    }

    public ArrayListCallback() {
        this(new ArrayList<T>());
    }

    public ArrayListCallback(ArrayList<T> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public void call(T t) {
        arrayList.add(t);
    }
}
