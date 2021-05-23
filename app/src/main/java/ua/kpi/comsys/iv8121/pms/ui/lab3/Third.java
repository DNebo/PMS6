package ua.kpi.comsys.iv8121.pms.ui.lab3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.daimajia.swipe.SwipeLayout;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import ua.kpi.comsys.iv8121.pms.R;

import static android.graphics.BitmapFactory.decodeStream;
import static android.widget.Toast.makeText;

public class Third extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static View root;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout bookList;
    private static HashMap<SwipeLayout, classBook> booksLinear;
    @SuppressLint("StaticFieldLeak")
    private static TextView noItems;
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar loadingBar;
    private static Set<SwipeLayout> removeSet;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.l3_lay, container, false);
        setRetainInstance(true);
        bookList = root.findViewById(R.id.scroll_lay);
        booksLinear = new HashMap<>();

        noItems = root.findViewById(R.id.no_books_view);
        loadingBar = root.findViewById(R.id.no_items_progressbar);

        removeSet = new HashSet<>();

        SearchView searchView = root.findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                removeSet.addAll(booksLinear.keySet());
                if (query.length() >= 3) {
                    AsyncLoadBooks aTask = new AsyncLoadBooks();
                    loadingBar.setVisibility(View.VISIBLE);
                    noItems.setVisibility(View.GONE);
                    aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
                }
                else {
                    for (SwipeLayout swipeLayout : removeSet) {
                        binClicked(swipeLayout);
                    }
                    removeSet.clear();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                removeSet.addAll(booksLinear.keySet());
                if (query.length() >= 3) {
                    AsyncLoadBooks aTask = new AsyncLoadBooks();
                    loadingBar.setVisibility(View.VISIBLE);
                    noItems.setVisibility(View.GONE);
                    aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
                }
                else {
                    for (SwipeLayout swipeLayout : removeSet) {
                        binClicked(swipeLayout);
                    }
                    removeSet.clear();
                }
                return false;
            }
        });

        Button btnAddBook = root.findViewById(R.id.button_add_book);
        btnAddBook.setOnClickListener(v -> {
            BookAdd popUpClass = new BookAdd();
            Object[] popups = popUpClass.showPopupWindow(v);

            View popupView = (View) popups[0];
            PopupWindow popupWindow = (PopupWindow) popups[1];

            EditText inputTitle = popupView.findViewById(R.id.input_title);
            EditText inputSubtitle = popupView.findViewById(R.id.input_subtitle);
            EditText inputPrice = popupView.findViewById(R.id.input_price);

            Button buttonAdd = popupView.findViewById(R.id.button_add_add);
            buttonAdd.setOnClickListener(v1 -> {
                if (inputTitle.getText().toString().length() != 0 &&
                        inputSubtitle.getText().toString().length() != 0 &&
                        inputPrice.getText().toString().length() != 0) {
                    Object[] tmp = new BookShelf(root.getContext(), bookList,
                            new classBook(inputTitle.getText().toString(),
                                    inputSubtitle.getText().toString(),
                                    inputPrice.getText().toString())).bookShelf;

                    booksLinear.put((SwipeLayout) tmp[0], (classBook)tmp[1]);
                    changeLaySizes();
                    noItems.setVisibility(View.GONE);

                    popupWindow.dismiss();
                }
                else{
                    makeText(getActivity(), "Incorrect data!",
                            Toast.LENGTH_LONG).show();
                }
            });
        });

        changeLaySizes();

        return root;
    }

    protected static void loadBooks(ArrayList<classBook> classBooks){
        if (classBooks != null) {
            for (SwipeLayout swipeLayout : removeSet) {
                binClicked(swipeLayout);
            }
            removeSet.clear();
            if (classBooks.size() > 0) {
                noItems.setVisibility(View.GONE);
                for (classBook classBook :
                        classBooks) {
                    Object[] tmp = new BookShelf(root.getContext(), bookList, classBook).bookShelf;

                    booksLinear.put((SwipeLayout) tmp[0], (classBook)tmp[1]);
                }
            } else {
                noItems.setVisibility(View.VISIBLE);
            }
        }
        else {
            noItems.setVisibility(View.VISIBLE);
            makeText(root.getContext(), "Cannot load data!", Toast.LENGTH_LONG).show();
        }
        loadingBar.setVisibility(View.GONE);
    }

    public static void binClicked(SwipeLayout swipeLayout){
        booksLinear.remove(swipeLayout);
        bookList.removeView(swipeLayout);
        if (booksLinear.keySet().isEmpty()){
            noItems.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        changeLaySizes();
    }

    private void changeLaySizes(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) root.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        for (SwipeLayout bookshelf :
                booksLinear.keySet()) {
            ((ConstraintLayout)bookshelf.getChildAt(1)).getChildAt(0).setLayoutParams(
                    new ConstraintLayout.LayoutParams(width/3, width/3));
        }
    }

    private static class AsyncLoadBooks extends AsyncTask<String, Void, ArrayList<classBook>> {
        private String getRequest(String url){
            StringBuilder result = new StringBuilder();
            try {
                URL getReq = new URL(url);
                URLConnection bookConnection = getReq.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(bookConnection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                    result.append(inputLine).append("\n");

                in.close();

            } catch (MalformedURLException e) {
                System.err.println(String.format("Incorrect URL <%s>!", url));
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        private ArrayList<classBook> parseBooks(String jsonText) throws ParseException {
            ArrayList<classBook> result = new ArrayList<>();

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);

            JSONArray books = (JSONArray) jsonObject.get("books");
            for (Object book : Objects.requireNonNull(books)) {
                JSONObject tmp = (JSONObject) book;
                result.add(new classBook(
                        (String) tmp.get("title"),
                        (String) tmp.get("subtitle"),
                        (String) tmp.get("isbn13"),
                        (String) tmp.get("price"),
                        (String) tmp.get("image")
                ));
            }

            return result;
        }
        private ArrayList<classBook> search(String newText){
            String jsonResponse = String.format("https://api.itbook.store/1.0/search/\"%s\"", newText);
            try {
                ArrayList<classBook> classBooks;
                classBooks = parseBooks(getRequest(jsonResponse));
                return classBooks;
            } catch (ParseException e) {
                System.err.println("Incorrect content of JSON file!");
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected ArrayList<classBook> doInBackground(String... strings) {
            return search(strings[0]);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(ArrayList<classBook> classBooks) {
            super.onPostExecute(classBooks);
            Third.loadBooks(classBooks);
        }
    }

    public static class DownloadIm extends AsyncTask<String, Void, Bitmap> {
        @SuppressLint("StaticFieldLeak")
        ImageView bmImage;
        @SuppressLint("StaticFieldLeak")
        ProgressBar loadingBar;
        @SuppressLint("StaticFieldLeak")
        Context context;

        public DownloadIm(ImageView bmImage, ProgressBar loadingBar, Context context) {
            this.bmImage = bmImage;
            this.loadingBar = loadingBar;
            this.context = context;
        }

        protected Bitmap doInBackground(String... urls) {
            String u = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in;
                in = new URL(u).openStream();
                mIcon11 = decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null)
                bmImage.setImageBitmap(result);
            else {
                bmImage.setBackgroundResource(R.drawable.unnamed);
                makeText(context, "Cannot load data!", Toast.LENGTH_LONG).show();
            }
            loadingBar.setVisibility(View.GONE);
            bmImage.setVisibility(View.VISIBLE);
        }
    }
}