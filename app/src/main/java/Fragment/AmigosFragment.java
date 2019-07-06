package Fragment;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jpantas.sspeach.MainActivity;
import com.example.jpantas.sspeach.R;
import com.example.jpantas.sspeach.AmigosViewHolder;
import com.facebook.login.widget.ProfilePictureView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import MAIN_CLASSES.Chat;
import MAIN_CLASSES.Friend;
import MAIN_CLASSES.Group;
import MAIN_CLASSES.User;

import static com.example.jpantas.sspeach.MainActivity.adapter;
import static com.example.jpantas.sspeach.MainActivity.viewPager;
import static com.facebook.FacebookSdk.getApplicationContext;

public class AmigosFragment extends Fragment {
    /*
 implements Dialog.EditNameDialogListener {*/

    public AmigosFragment() {
    }

    EditText groupnameinput;
    String groupname, mCurrent_user_id, friendname, frienduid;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef, mUsersDatabase, mRefGroups;
    private List<User> users;
    HashMap<String, Boolean> groupusers;
    int row_index = -1;
    RecyclerView mRecyclerView;
    FloatingActionButton createGroupBtn;
    Button saveGroupBtn;
    Group grouptosave;
    String groupkey;
    private FirebaseAuth mAuth;
    ImageView uploadPhoto;
    public static final int GALLERY = 1;
    public static final int CAMERA = 2;
    Uri contentURI;
    FirebaseStorage storage;
    StorageReference storageReference;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //TODO limit of 15 letters
        View rootView = inflater.inflate(R.layout.fragment_amigos, container, false);

        //TODO read groups images
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Friends").child(mCurrent_user_id);
        mRef.keepSynced(true);
        mRefGroups = mFirebaseDatabase.getReference("Groups");
        mRefGroups.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        groupusers = new HashMap<>();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //add current user to group
        groupusers.put(mCurrent_user_id, true);

        mRecyclerView = rootView.findViewById(R.id.amigos_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        createGroupBtn = rootView.findViewById(R.id.createGroupBtn);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("NAMASTE entrou", "amigos");

        FirebaseRecyclerOptions<Friend> options =
                new FirebaseRecyclerOptions.Builder<Friend>()
                        .setQuery(mRef, Friend.class)
                        .build();

        //TODO make query for the users that are friends. OR search in database for friends class
        final FirebaseRecyclerAdapter<Friend, AmigosViewHolder> fra = new FirebaseRecyclerAdapter<Friend, AmigosViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final AmigosViewHolder holder, final int position, @NonNull final Friend model) {

                Log.d("NAMASTE user name", getRef(position).getKey());
                frienduid = getRef(position).getKey();

                mUsersDatabase.child(frienduid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        friendname = dataSnapshot.child("name").getValue().toString();
                        String uri = dataSnapshot.child("uri").getValue().toString();
                        holder.setUser(getApplicationContext(), friendname, uri);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //TODO change text when invite/remove friend is clicked (lapit)
                //TODO send invite
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("NAMASTE", "selected");
                        //add/remove user for the new group
                        if (model.isSelected()) {
                            Log.d("NAMASTE members", "removed");
                            Log.d("NAMASTE members", getRef(position).getKey());

                            groupusers.remove(getRef(position).getKey());
                        } else {
                            Log.d("NAMASTE members", "added");
                            Log.d("NAMASTE members", getRef(position).getKey());

                            groupusers.put(getRef(position).getKey(), true);
                        }
                        model.setSelected(!model.isSelected());
                        holder.itemView.setBackgroundColor(model.isSelected() ? getResources().getColor(R.color.colorPurple) : Color.WHITE);

                        //TODO change to more than 2 rather than more than 0
                        // groupuser with more than 2 elements enables the start of a group
                        if (groupusers.size() > 2) {
                            createGroupBtn.setEnabled(true);
                            createGroupBtn.show();
                        } else if (groupusers.size() == 1) {
                            Toast.makeText(getActivity(), "You need to select one more element", Toast.LENGTH_LONG).show();
                            createGroupBtn.setEnabled(false);
                            createGroupBtn.hide();
                        } else {
                            Toast.makeText(getActivity(), "You need to select two or more friends to start a group", Toast.LENGTH_LONG).show();
                            createGroupBtn.setEnabled(false);
                            createGroupBtn.hide();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public AmigosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cell_friend, parent, false);

                return new AmigosViewHolder(view);
            }
        };
        fra.startListening();
        mRecyclerView.setAdapter(fra);

        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fra.stopListening();
                fra.startListening();
                mRecyclerView.setAdapter(fra);
                createGroupBtn.hide();
                newGroup();
            }
        });

    }

    public static AmigosFragment newInstance() {
        return new AmigosFragment();
    }


    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Photo");
        String[] pictureDialogItems = {
                "Gallery",
                "Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                                        == PackageManager.PERMISSION_DENIED){
                                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);

                                }
                                else {
                                    takePhotoFromCamera();

                                }
                                    break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }}//end onRequestPermissionsResult

    private void newGroup() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("New Group");
        dialog.setContentView(R.layout.dialog_newgroup);
        groupnameinput = dialog.findViewById(R.id.groupname);
        saveGroupBtn = dialog.findViewById(R.id.saveBtn);
        uploadPhoto = dialog.findViewById(R.id.uploadPhoto);

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });


        groupnameinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                groupname = charSequence.toString();
                Log.d("NAMASTE group name", String.valueOf(groupname.length()));
                if (groupname.length() > 1 && uploadPhoto.getDrawable() != null) {
                    saveGroupBtn.setEnabled(true);
                    saveGroupBtn.setVisibility(View.VISIBLE);
                } else{
                    saveGroupBtn.setEnabled(false);
                    saveGroupBtn.setVisibility(View.INVISIBLE);
                    Log.d("NAMASTE ", "entrou");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        saveGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(contentURI != null)
                {
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();


                    final StorageReference ref = storageReference.child("images/groups/"+ UUID.randomUUID().toString() + ".png");
                    ref.putFile(contentURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downUri = task.getResult();
                                progressDialog.dismiss();
                                grouptosave = new Group();
                                grouptosave.setMembers(groupusers);
                                grouptosave.setName(groupname);
                                grouptosave.setCreator(mCurrent_user_id);
                                grouptosave.setUri(downUri.toString());

                                //save group to firebase
                                groupkey = mRef.push().getKey();
                                mRefGroups.child(groupkey).setValue(grouptosave);
                                dialog.dismiss();
                                viewPager.setCurrentItem(2);

                                Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();

                                Log.d("NAMASTE", "onComplete: Url: "+ downUri.toString());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();

                    uploadPhoto.setDrawingCacheEnabled(true);
                    uploadPhoto.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) uploadPhoto.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    final StorageReference ref = storageReference.child("images/groups/"+ UUID.randomUUID().toString() + ".png");
                    ref.putBytes(data).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw task.getException();
                            }
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downUri = task.getResult();
                                progressDialog.dismiss();
                                grouptosave = new Group();
                                grouptosave.setMembers(groupusers);
                                grouptosave.setName(groupname);
                                grouptosave.setCreator(mCurrent_user_id);
                                grouptosave.setUri(downUri.toString());

                                //save group to firebase
                                groupkey = mRef.push().getKey();
                                mRefGroups.child(groupkey).setValue(grouptosave);
                                dialog.dismiss();
                                viewPager.setCurrentItem(1);

                                Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();

                                Log.d("NAMASTE", "onComplete: Url: "+ downUri.toString());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });



                }


            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }

   /* @Override
    public void onFinishEditDialog(String inputText) {
        Toast.makeText(getApplicationContext(), "Hi, " + inputText, Toast.LENGTH_SHORT).show();


    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (data != null) {
                contentURI = data.getData();
                Log.d("NAMASTE uri", contentURI.toString());

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    uploadPhoto.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            uploadPhoto.setImageBitmap(thumbnail);
            Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }

        Log.d("NAMASTE group name", String.valueOf(groupname.length()));

        if (groupname.length() > 1 && uploadPhoto.getDrawable() != null) {
            saveGroupBtn.setEnabled(true);
            saveGroupBtn.setVisibility(View.VISIBLE);
        } else{
            saveGroupBtn.setEnabled(false);
            saveGroupBtn.setVisibility(View.INVISIBLE);
            Log.d("NAMASTE ", "entrou");

        }

    }

}
