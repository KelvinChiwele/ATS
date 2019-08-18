package com.techart.atszambia.constants;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.techart.atszambia.R;
import com.techart.atszambia.models.Chemical;
import com.techart.atszambia.models.News;
import com.techart.atszambia.models.Products;
import com.techart.atszambia.models.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kelvin on 11/09/2017.
 */

public final class FireBaseUtils {
    public static final ArrayList<String> staff =
            new ArrayList<>(Arrays.asList(
                    "mtaziwa@atszambia.co.zm",
                    "moses@atszambia.co.zm",
                    "tamara@atszambia.co.zm",
                    "kelvin@atszambia.co.zm",
                    "eric@atszambia.co.zm",
                    "brian@atszambia.co.zm",
                    "bruce@atszambia.co.zm",
                    "jack@atszambia.co.zm",
                    "atsappservice@atszambia.co.zm",
                    "williammwaya@atszambia.co.zm",
                    "peter@atszambia.co.zm"));

    public static final DatabaseReference mDatabaseChemicals = FirebaseDatabase.getInstance().getReference().child(Constants.CHEMICALS_KEY);
    public static final DatabaseReference mDatabaseProducts = FirebaseDatabase.getInstance().getReference().child(Constants.PRODUCTS);
    public static final DatabaseReference mDatabasePests = FirebaseDatabase.getInstance().getReference().child(Constants.PESTS_KEY);
    public static final DatabaseReference mDatabaseQuestions = FirebaseDatabase.getInstance().getReference().child(Constants.QUESTIONS_KEY);
    public static final DatabaseReference mDatabaseAnswers = FirebaseDatabase.getInstance().getReference().child(Constants.ANSWERS_KEY);
    public static final DatabaseReference mDatabaseResources = FirebaseDatabase.getInstance().getReference().child(Constants.RESOURCES_KEY);

    public static final DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child(Constants.USERS_KEY);

    public static final DatabaseReference mDatabaseCrops = FirebaseDatabase.getInstance().getReference().child(Constants.CROPS_KEY);
    public static final DatabaseReference mDatabasePrograms = FirebaseDatabase.getInstance().getReference().child(Constants.PROGRAMS_KEY);

    public static final DatabaseReference mDatabaseDiseases = FirebaseDatabase.getInstance().getReference().child(Constants.DISEASES_KEY);

    public static final DatabaseReference mDatabasePest = FirebaseDatabase.getInstance().getReference().child(Constants.PEST_KEY);
    public static final DatabaseReference mDatabaseEfektoPest = FirebaseDatabase.getInstance().getReference().child(Constants.EFEKTO_PEST_KEY);
    public static final DatabaseReference mDatabaseCaseStudy = FirebaseDatabase.getInstance().getReference().child(Constants.CASE_STUDY_KEY);

    public static final DatabaseReference mDatabaseNewsViews = FirebaseDatabase.getInstance().getReference().child(Constants.NEWS_VIEWS_KEY);
    public static final DatabaseReference mDatabaseProductViews = FirebaseDatabase.getInstance().getReference().child(Constants.PRODUCT_VIEWS_KEY);
    public static final DatabaseReference mDatabaseResourceViews = FirebaseDatabase.getInstance().getReference().child(Constants.RESOURCE_VIEWS_KEY);
    public static final DatabaseReference mDatabaseDirectory = FirebaseDatabase.getInstance().getReference().child(Constants.DIRECTORY_KEY);
    public static final DatabaseReference mDatabaseComments = FirebaseDatabase.getInstance().getReference().child(Constants.COMMENT_KEY);
    public static final DatabaseReference mDatabaseNews = FirebaseDatabase.getInstance().getReference().child(Constants.NEWS_KEY);
    public static final DatabaseReference mDatabaseNotifications = FirebaseDatabase.getInstance().getReference().child(Constants.NOTIFICATION_KEY);
    public static final DatabaseReference mDatabaseVersion = FirebaseDatabase.getInstance().getReference().child(Constants.VERSION_KEY);
    public static final DatabaseReference mDatabaseStamp = FirebaseDatabase.getInstance().getReference().child(Constants.STAMP_KEY);


    public static final DatabaseReference mDatabaseReviews = FirebaseDatabase.getInstance().getReference().child(Constants.REVIEWS_KEY);

    //Storage
    public static final StorageReference mStorageQuestions = FirebaseStorage.getInstance().getReference().child(Constants.QUESTION);
    public static final StorageReference mStorageNews = FirebaseStorage.getInstance().getReference().child(Constants.NEWS_KEY);
    public static final StorageReference mStorageDiseaase = FirebaseStorage.getInstance().getReference().child(Constants.DISEASES_KEY);
    public static final StorageReference mStorageCaseStudy = FirebaseStorage.getInstance().getReference().child(Constants.CASE_STUDY_KEY);
    public static final StorageReference mStoragePests = FirebaseStorage.getInstance().getReference();

    //File
    private FireBaseUtils()
    {
    }

    @NonNull
    public static String getAuthor() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    @NonNull
    public static String getUiD() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    public static String getEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    public static void updateNotifications(String category, String product, String action, String postUrl, String message, String imageUrl) {
        String url = mDatabaseNotifications.push().getKey();
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.MESSAGE,message);
        values.put(Constants.ACTION,action);
        values.put(Constants.USER_URL,getUiD());
        values.put(Constants.IMAGE_URL,imageUrl);
        values.put(Constants.CATEGORY,category);
        values.put(Constants.PRODUCT, product);
        values.put(Constants.EMAIL, getEmail());
        values.put(Constants.USER_NAME, getAuthor());
        values.put(Constants.POST_KEY, postUrl);
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        mDatabaseNotifications.child(url).setValue(values);
        stamp();
    }

    public static void stamp() {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        mDatabaseStamp.child("-LIaZ1jO8SXe4peE3JAc").setValue(values);
    }

    public static void onNewsViewed(String post_key) {
        mDatabaseNews.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                News news = mutableData.getValue(News.class);
                if (news == null) {
                    return Transaction.success(mutableData);
                }
                news.setNumViews(news.getNumViews() + 1);
                // Set value and report transaction success
                mutableData.setValue(news);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    public static boolean isAllowed(String email){
        return staff.contains(email);
    }

    public static void addNewsView(News model, String post_key) {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.USER_URL,getUiD());
        values.put(Constants.USER_NAME, FireBaseUtils.getAuthor());
        values.put(Constants.POST_TITLE, model.getNewsTitle());
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        mDatabaseNewsViews.child(post_key).child(getUiD()).setValue(values);
    }

    public static void setPostViewed(final String post_key, final ImageView btViewed){
        mDatabaseNewsViews.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(getUiD()).hasChild(Constants.USER_URL))                {
                    btViewed.setImageResource(R.drawable.ic_visibility_blue_24px);
                } else {
                    btViewed.setImageResource(R.drawable.ic_visibility_grey_24px);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void onQuestionAnswered(String post_key) {
        mDatabaseQuestions.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Question question = mutableData.getValue(Question.class);
                if (question == null) {
                    return Transaction.success(mutableData);
                }
                question.setAnswerCount(question.getAnswerCount() + 1 );
                // Set value and report transaction success
                mutableData.setValue(question);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    public static void onChemicalReviewed(String post_key) {
        mDatabaseChemicals.child(post_key).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Chemical chemical = mutableData.getValue(Chemical.class);
                if (chemical == null) {
                    return Transaction.success(mutableData);
                }
                chemical.setNumReviews(chemical.getNumReviews() + 1 );
                // Set value and report transaction success
                mutableData.setValue(chemical);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    //Products
    public static void onProductsViewed(final String category) {
        mDatabaseProducts.child(category).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Products products = mutableData.getValue(Products.class);
                if (products == null) {
                    return Transaction.success(mutableData);
                } else if (products.getClients() ==  null){
                    Map<String,Object> values = new HashMap<>();
                    values.put(Constants.CLIENTS,1);
                    mDatabaseProducts.child(category).updateChildren(values);
                    return Transaction.success(mutableData);
                }
                products.setClients(products.getClients() + 1);
                // Set value and report transaction success
                mutableData.setValue(products);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    public static void onProductsClicks(final String category) {
        mDatabaseProducts.child(category).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Products products = mutableData.getValue(Products.class);
                if (products == null) {
                    return Transaction.success(mutableData);
                } else if (products.getClicks() ==  null){
                    Map<String,Object> values = new HashMap<>();
                    values.put(Constants.CLICKS,1);
                    mDatabaseProducts.child(category).updateChildren(values);
                    return Transaction.success(mutableData);
                }
                products.setClicks(products.getClicks() + 1);
                // Set value and report transaction success
                mutableData.setValue(products);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    public static void setProductsViewed(final String category, final ImageView btViewed){
        mDatabaseProductViews.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null && dataSnapshot.child(getUiD()).hasChild(Constants.USER_URL)){
                    btViewed.setImageResource(R.drawable.ic_visibility_blue_24px);
                } else {
                    btViewed.setImageResource(R.drawable.ic_visibility_grey_24px);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void addProductsView(String category) {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.USER_URL,getUiD());
        values.put(Constants.USER_NAME, getAuthor());
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        mDatabaseProductViews.child(category).child(getUiD()).setValue(values);
    }

    //Resources
    public static void onResourceViewed(final String category) {
        mDatabaseResources.child(category).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Products products = mutableData.getValue(Products.class);
                if (products == null) {
                    return Transaction.success(mutableData);
                } else if (products.getClients() ==  null){
                    Map<String,Object> values = new HashMap<>();
                    values.put(Constants.CLIENTS,1);
                    mDatabaseResources.child(category).updateChildren(values);
                    return Transaction.success(mutableData);
                }
                products.setClients(products.getClients() + 1);
                // Set value and report transaction success
                mutableData.setValue(products);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    public static void onResourceClicks(final String category) {
        mDatabaseResources.child(category).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Products products = mutableData.getValue(Products.class);
                if (products == null) {
                    return Transaction.success(mutableData);
                } else if (products.getClicks() ==  null){
                    Map<String,Object> values = new HashMap<>();
                    values.put(Constants.CLICKS,1);
                    mDatabaseResources.child(category).updateChildren(values);
                    return Transaction.success(mutableData);
                }
                products.setClicks(products.getClicks() + 1);
                // Set value and report transaction success
                mutableData.setValue(products);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }

    public static void setResourceViewed(final String category, final ImageView btViewed){
        mDatabaseResourceViews.child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null && dataSnapshot.child(getUiD()).hasChild(Constants.USER_URL)){
                    btViewed.setImageResource(R.drawable.ic_visibility_blue_24px);
                } else {
                    btViewed.setImageResource(R.drawable.ic_visibility_grey_24px);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void addResourceView(String category) {
        Map<String,Object> values = new HashMap<>();
        values.put(Constants.USER_URL,getUiD());
        values.put(Constants.USER_NAME, getAuthor());
        values.put(Constants.TIME_CREATED, ServerValue.TIMESTAMP);
        mDatabaseResourceViews.child(category).child(getUiD()).setValue(values);
    }
}