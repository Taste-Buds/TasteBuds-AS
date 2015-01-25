package com.codepath.apps.tastebuds.fragments;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.apps.tastebuds.R;
import com.codepath.apps.tastebuds.models.RestaurantReview;
import com.codepath.apps.tastebuds.models.Tag;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment.OnEmojiconClickedListener;
import com.rockerhieu.emojicon.EmojiconsDialogFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class RestaurantReviewDialog extends DialogFragment implements OnEmojiconClickedListener, EmojiconsDialogFragment.OnEmojiconBackspaceClickedListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		  setEmojiconFragment(false);
	}

	private TextView tvRestaurantName;
	private TextView tvTags;
	//private AutoCompleteTextView etTags;
	EmojiconEditText mEditEmojicon;
	EmojiconsDialogFragment nf;
	//private TextView etWords;
	private EditText etReview;
	private RatingBar rbRating;
	private Button btnTaste;
	private ImageButton btnCancel;
	private String restaurantName;
	private String restaurantId;
	public RestaurantReviewDialogListener listener;
	private List<String> tagStrings;
	

    public static RestaurantReviewDialog newInstance(String placesId, String restaurantName) {
    	RestaurantReviewDialog dialog = new RestaurantReviewDialog();
        Bundle args = new Bundle();
        args.putString("restaurant_id", placesId);
        args.putString("restaurant_name", restaurantName);
        dialog.setArguments(args);
        
        return dialog;
    }
    
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
        .getAttributes().windowAnimations = R.style.DialogAnimationTop;
    }

	private void setEmojiconFragment(boolean useSystemDefault) {
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		nf = (EmojiconsDialogFragment) EmojiconsDialogFragment.newInstance(useSystemDefault);
		ft.add(R.id.restEmojicons, nf,"main");
		ft.hide(nf);
		ft.commit();
	}

    public interface RestaurantReviewDialogListener {
    	void onFinishReviewComposeDialog(RestaurantReview review);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_compose, container);
      
        restaurantName = getArguments().getString("restaurant_name");
        restaurantId = getArguments().getString("restaurant_id");

        tvRestaurantName = (TextView) view.findViewById(R.id.tvRestaurantNameCompose);
        tvRestaurantName.setText(restaurantName);
        tvTags = (TextView) view.findViewById(R.id.tvComposeTags);
        mEditEmojicon = (EmojiconEditText) view.findViewById(R.id.etComposeTags);
        etReview = (EditText) view.findViewById(R.id.etComposeReview);
        rbRating = (RatingBar) view.findViewById(R.id.rbComposeReviewRatings);
		LayerDrawable stars = (LayerDrawable) rbRating.getProgressDrawable();
		//stars.getDrawable(0).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
		//stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
		stars.getDrawable(2).setColorFilter(Color.argb(255, 255, 153, 51),
				PorterDuff.Mode.SRC_ATOP);

        btnTaste = (Button) view.findViewById(R.id.btnComposeReview);
        btnTaste.setGravity(android.view.Gravity.CENTER);
        btnCancel = (ImageButton) view.findViewById(R.id.btnComposeBack);

//       ivKeyboard.setOnClickListener(new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			toggleEmojiKeyboard();
//			
//		}
//	});
        /*etWords = (TextView) view.findViewById(R.id.etWords);
        final TextWatcher txwatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				etWords.setText(Html.fromHtml("<font color=\"#FFFFFF\" type=\"roboto\">" + 
						String.valueOf(420 - s.length()) + " | </font>"));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				etWords.setText(Html.fromHtml("<font color=\"#606060\" type=\"roboto\">420 | </font>"));
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		};
		etReview.addTextChangedListener(txwatcher);*/
//       ivKeyboard.setOnClickListener(new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			toggleEmojiKeyboard();
//			
//		}
//	});

        rbRating.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideEmojis();
				return false;
			}
		});
        
        etReview.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideEmojis();
				return false;
			}
		});
        OnTouchListener otl = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideSoftKeyBoard();	 
				showEmojis();
			//	mEditEmojicon.requestFocus();
				return true;
			}
        };
        mEditEmojicon.setOnTouchListener(otl);
		mEditEmojicon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showEmojis();
				hideSoftKeyBoard();
			}
		});

		mEditEmojicon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// Dismiss Keyboard
					hideSoftKeyBoard();	    	   
					return true;
				}
				return false;
			}
		});


        
		tagStrings = new ArrayList<String>();
		ParseQuery<Tag> query = Tag.getQuery();
		query.findInBackground(new FindCallback<Tag>() {
			@Override
			public void done(List<Tag> tags, ParseException e) {
				for (Tag tag : tags) {
					tagStrings.add(tag.getTag());
				}
				ArrayAdapter<String> tagsAdapter = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_1, tagStrings);
				//mEditEmojicon.setAdapter(tagsAdapter);
			}
		});

        btnTaste.setOnClickListener(new View.OnClickListener() {
        	@Override
            public void onClick(View v) {
        		hideSoftKeyBoard();
        		RestaurantReview review = new RestaurantReview();
        		review.setText(etReview.getText().toString());
        		review.setRating(rbRating.getRating());
        		review.setGooglePlacesId(restaurantId);
        		review.setUser(ParseUser.getCurrentUser());
        		review.setTags(restaurantId, mEditEmojicon.getText().toString());
        		review.setRestaurantName(restaurantName);
        		
        		listener.onFinishReviewComposeDialog(review);
        		getDialog().dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    listener.onFinishReviewComposeDialog(null);
				getDialog().dismiss();
			}
		});

    	Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setGravity(Gravity.CENTER);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }
    private void toggleEmojiKeyboard(){
    	if(nf.isHidden()){
    		showEmojis();
    	}else{
    		hideEmojis();
    	}
    }
    private void hideEmojis(){
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		ft.hide(nf);
		ft.commit();
    }

    private void showEmojis(){
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		ft.show(nf);
		ft.commit();
    }
	private void hideSoftKeyBoard() {
		Context context = getActivity().getBaseContext();
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	//	if(imm.isAcceptingText()) {      
		imm.hideSoftInputFromWindow(mEditEmojicon.getWindowToken(), 0);
			//imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
	//	}
		
	}
	
	private void showSoftKeyBoard() {
		Context context = getActivity().getBaseContext();
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm != null) {     
			imm.showSoftInput(mEditEmojicon, InputMethodManager.SHOW_IMPLICIT);
		}
	}
    @Override
    public void onStart() {
    	super.onStart();
    	// safety check
    	if (getDialog() == null) {
    		return;
    	}
    	getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
    			ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsDialogFragment.input(mEditEmojicon, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsDialogFragment.backspace(mEditEmojicon);
    }
}
