package com.jesttek.snakeWar;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer.ReliableMessageSentCallback;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;
import com.jesttek.snakeWar.Inferfaces.IMoveReceiver;
import com.jesttek.snakeWar.Inferfaces.IPlayServices;

public class MainActivity extends AndroidApplication implements IPlayServices, GameHelperListener, RoomUpdateListener, ReliableMessageSentCallback, RealTimeMessageReceivedListener, RoomStatusUpdateListener, OnInvitationReceivedListener {

	private static final String AD_UNIT_ID = "ca-app-pub-2046409282727343/7361362519";
	private static final String LEADERBOARD_ID = "CgkIsc2GhfcPEAIQAw";
	
	// Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;
    final static String TAG = "SnakeWar";    
    //final static String TESTTAG = "SnakeWarTest";    
	final static int MAX_PLAYERS = 2;	
	
	public static Context ApplicationContext;
	private Context mContext;
	public static Handler UIThread = new Handler();
	private static InterstitialAd mInterstitialAd;
	private ProgressDialog mLoadingDialog;
	private boolean mConnected = false;
	protected GameHelper mHelper;
	private SnakeWarGame mGame = new SnakeWarGame();	
	private String mRoomId = null; // Room ID where the currently active game is taking place; null if we're not playing.	
	private ArrayList<Participant> mParticipants = null; // The participants in the currently active game
	private String mMyId = null; // My participant ID in the currently active game
	private String mIncomingInvitationId = null; // If non-null, this is the id of the invitation we received via the invitation listener	
    private IMoveReceiver mMoveReceiver;
    private Date mPingTimer;
    private boolean mHosting = false;
    private boolean mOpponentReady = false;
    private Participant mOpponent;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        mHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        mHelper.enableDebugLog(true);
    	mHelper.setup(this);
    	mHelper.setMaxAutoSignInAttempts(1);    
        SnakeWarGame.SaveController = new AndroidSaveData(this);
        SnakeWarGame.PlayServices = this;
    	mContext = this;
        ApplicationContext = this.getApplicationContext();
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();     
        SnakeWarGame.VIRTUAL_HEIGHT = 768;
        SnakeWarGame.VIRTUAL_WIDTH = 1280;      
        initialize(mGame, cfg);
        setupInterstitial();
    }
    
    // Activity just got to the foreground. 
    @Override
    public void onStart()
    {
    	mHelper.onStart(this);
    	super.onStart();
    }

    @Override
    public void onStop()
    {
		// if we're in a room, leave it.
		leaveRoom();
		
		// stop trying to keep the screen on
		stopKeepingScreenOn();
		
    	mHelper.onStop();
    	super.onStop();
    }

    @Override
	public void onSignInFailed() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSignInSucceeded() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void logon() {
        try {
            runOnUiThread(new Runnable() {
                    // @Override
                    public void run() {
                            mHelper.beginUserInitiatedSignIn();
                    }
            });
	    } catch (final Exception ex) {
	    	Log.w(TAG,"Failed to logon:" + ex.getMessage());
	    }
	}

	@Override
	public void logoff() {
        try {
            runOnUiThread(new Runnable() {
                    // @Override
                    public void run() {
                            mHelper.signOut();
                    }
            });
	    } catch (final Exception ex) {
	    	Log.w(TAG,"Failed to logoff:" + ex.getMessage());
	
	    }
		
	}

	@Override
	public void unlockAchievement(String achievementID) {
		if (mHelper.isSignedIn())
		{
			Games.Achievements.unlock(mHelper.getApiClient(), achievementID);
		}		
	}

	@Override
	public void showLeaderboards() {
    	
        if (mHelper.isSignedIn()) {  
        	startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mHelper.getApiClient(),LEADERBOARD_ID),9002);
        } 
        else 
        {
        	this.runOnUiThread(new Runnable(){
        	    public void run(){
        	    	Toast.makeText(ApplicationContext, "Sign in to view leaderboard", Toast.LENGTH_LONG).show();      
        	    }
        	});
        }	
	}

	@Override
	public void showAchievements() {

        if (mHelper.isSignedIn()) {
        	startActivityForResult(Games.Achievements.getAchievementsIntent(mHelper.getApiClient()),9002);
	    } else {
        	this.runOnUiThread(new Runnable(){
        	    public void run(){
        	    	Toast.makeText(ApplicationContext, "Sign in to view achievements", Toast.LENGTH_LONG).show();      
        	    }
        	});	
	    }		
	}

	@Override
	public boolean isSignedIn() {
		return mHelper.isSignedIn();
	}
	
	@Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
		mHelper.onActivityResult(requestCode, responseCode, intent);	

        switch (requestCode) {
        case RC_SELECT_PLAYERS:
            // we got the result from the "select players" UI -- ready to create the room
            handleSelectPlayersResult(responseCode, intent);
            break;
        case RC_INVITATION_INBOX:
            // we got the result from the "select invitation" UI (invitation inbox). We're
            // ready to accept the selected invitation:
            handleInvitationInboxResult(responseCode, intent);
            break;
        case RC_WAITING_ROOM:
            // we got the result from the "waiting room" UI.
            if (responseCode == Activity.RESULT_OK) {
                // ready to start playing
        		Gdx.app.postRunnable(new Runnable() {
        			@Override
        			public void run() {
        		        startGame(true);
        			}
        		});
            } else if (responseCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player indicated that they want to leave the room
                leaveRoom();
            } else if (responseCode == Activity.RESULT_CANCELED) {
                // Dialog was cancelled (user pressed back key, for instance).
                leaveRoom();
            }
            break;
        }
    }
	
	// Handle the result of the "Select players UI" we launched when the user clicked the
    // "Invite friends" button. We react by creating a room with those players.
    private void handleSelectPlayersResult(int response, Intent data) 
    {
		if (response != Activity.RESULT_OK) {
			Log.w(TAG, "*** select players UI cancelled, " + response);
	    	mConnected = false;
			return;
		}
		
		// get the invitee list
		final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
		
		// get the automatch criteria
		Bundle autoMatchCriteria = null;
		int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
		int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
		if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) 
		{
			autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
		}
		
		// create the room
		RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
		rtmConfigBuilder.addPlayersToInvite(invitees);
		rtmConfigBuilder.setMessageReceivedListener(this);
		rtmConfigBuilder.setRoomStatusUpdateListener(this);
		if (autoMatchCriteria != null) {
		    rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
		}
		mConnected = false;
		keepScreenOn();
		Games.RealTimeMultiplayer.create(mHelper.getApiClient(), rtmConfigBuilder.build());
	}
    
	// Handle the result of the invitation inbox UI, where the player can pick an invitation
	// to accept. We react by accepting the selected invitation, if any.
    private void handleInvitationInboxResult(int response, Intent data) 
    {
		if (response != Activity.RESULT_OK) {
			Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
	    	mConnected = false;
			return;
		}
		
		Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);
		
		// accept invitation
		acceptInviteToRoom(inv.getInvitationId());
	}
    
	// Accept the given invitation.
    private void acceptInviteToRoom(String invId) 
	{
		// accept the invitation
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
		roomConfigBuilder.setInvitationIdToAccept(invId)
			.setMessageReceivedListener(this)
			.setRoomStatusUpdateListener(this);
		mConnected = false;
		keepScreenOn();
		Games.RealTimeMultiplayer.join(mHelper.getApiClient(), roomConfigBuilder.build());
	}   
	
	// Leave the room.
	private void leaveRoom() 
    {    	
    	stopKeepingScreenOn();
    	if (mRoomId != null) 
    	{        	
    		Games.RealTimeMultiplayer.leave(mHelper.getApiClient(), this, mRoomId);
    		mRoomId = null;
    	}
		mConnected = false;
    }
    
	// Show the waiting room UI to track the progress of other players as they enter the
	// room and get connected.
    private void showWaitingRoom(Room room) {
		// minimum number of players required for our game
		// For simplicity, we require everyone to join the game before we start it
		// (this is signaled by Integer.MAX_VALUE).
		final int MIN_PLAYERS = Integer.MAX_VALUE;
		Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mHelper.getApiClient(), room, MIN_PLAYERS);
		
		// show waiting room UI
		startActivityForResult(i, RC_WAITING_ROOM);
	}   
	
	// Called when we get an invitation to play a game. We react by showing that to the user.
	@Override
	public void onInvitationReceived(Invitation invitation) 
	{
		// We got an invitation to play a game! So, store it in
		// mIncomingInvitationId
		// and show the popup on the screen.
		mIncomingInvitationId = invitation.getInvitationId();
		Toast.makeText(this, invitation.getInviter().getDisplayName() + " " + getString(R.string.is_inviting_you), Toast.LENGTH_LONG).show();
		displayInvitation(true);  // This will show the invitation popup
	}
	
	@Override
	public void onInvitationRemoved(String invitationId) 
	{
		if (mIncomingInvitationId.equals(invitationId)) 
		{
			mIncomingInvitationId = null;
			displayInvitation(false); // This will hide the invitation popup
		}
	}
	
	// Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
    // is connected yet).
    @Override
    public void onConnectedToRoom(Room room) {

        // get room ID, participants and my ID:
        mRoomId = room.getRoomId();
        mParticipants = room.getParticipants();
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mHelper.getApiClient()));
    }

    // Called when we've successfully left the room (this happens a result of voluntarily leaving
    // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        // we have left the room; return to main screen.
    	mConnected = false;
    }

    // Called when we get disconnected from the room. We return to the main screen.
    @Override
    public void onDisconnectedFromRoom(Room room) {
		leaveRoom();
    }

    // Show error message about game being cancelled and return to main screen.
    private void showGameError(int errorCode) {
    	if(errorCode == GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED || errorCode == GamesStatusCodes.STATUS_TIMEOUT) {
        	Toast.makeText(this, getString(R.string.network_problem), Toast.LENGTH_LONG).show();    		
    	}
    	else {
	    	Toast.makeText(this, getString(R.string.game_problem), Toast.LENGTH_LONG).show();
    	}
    	mConnected = false;
    }

    // Called when room has been created
    @Override
    public void onRoomCreated(int statusCode, Room room) {
    	dismissLoadDialog();
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            showGameError(statusCode);
            return;
        }

        // show the waiting room UI
        showWaitingRoom(room);
    }

    // Called when room is fully connected.
    @Override
    public void onRoomConnected(int statusCode, Room room) {
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError(statusCode);
            return;
        }
        updateRoom(room);
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError(statusCode);
            return;
        }

        // show the waiting room UI
        showWaitingRoom(room);
    }

	// We treat most of the room update callbacks in the same way: we update our list of
	// participants and update the display. In a real game we would also have to check if that
	// change requires some action like removing the corresponding player avatar from the screen,
	// etc.
	@Override
	public void onPeerDeclined(Room room, List<String> arg1) {
		updateRoom(room);
	}
	
	@Override
	public void onPeerInvitedToRoom(Room room, List<String> arg1) {
		updateRoom(room);
	}
	
	@Override
	public void onP2PDisconnected(String participant) {
	}
	
	@Override
	public void onP2PConnected(String participant) {
	}
	
	@Override
	public void onPeerJoined(Room room, List<String> arg1) {
		updateRoom(room);
	}
	
	@Override
	public void onPeerLeft(Room room, List<String> peersWhoLeft) {
		if(mMoveReceiver != null) {
			mMoveReceiver.opponentDisconnected();
		}
		updateRoom(room);
	}
	
	@Override
	public void onRoomAutoMatching(Room room) {
		updateRoom(room);
	}
	
	@Override
	public void onRoomConnecting(Room room) {
		updateRoom(room);
	}
	
	@Override
	public void onPeersConnected(Room room, List<String> peers) {
		updateRoom(room);

		//check "host" by comparing user ids. User with the highest id is made "host"
		//The host is only used to send the start game message when both players have confirmed they're ready
		
		Participant p1 = mParticipants.get(0);
		Participant p2 = mParticipants.get(1);
		int compare = p1.getParticipantId().compareTo(p2.getParticipantId());
		if(compare > 0)
		{
			if(p1.getParticipantId().equals(mMyId))
			{
				mOpponent = p2;
				mHosting = true;
			}
			else
			{
				mOpponent = p1;
				mHosting = false;
			}
		}
		else
		{
			if(p1.getParticipantId().equals(mMyId))
			{
				mOpponent = p2;
				mHosting = false;
			}
			else
			{
				mOpponent = p1;
				mHosting = true;
			}
		}
	}
	
	@Override
	public void onPeersDisconnected(Room room, List<String> peers) {
		updateRoom(room);
	}
	
	private void updateRoom(Room room) {
		if (room != null) {
			mParticipants = room.getParticipants();
	    }
	}
	
	// Sets the flag to keep this screen on. It's recommended to do that during
	// the
	// handshake when setting up a game, because if the screen turns off, the
	// game will be
	// cancelled.
	private void keepScreenOn() {
	    runOnUiThread(new Runnable()
	    {

			@Override
			public void run() {
			    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);				
			}	    	
	    });
	}
    
	// Clears the flag that keeps the screen on.
	private void stopKeepingScreenOn() 
	{
	    runOnUiThread(new Runnable()
	    {
			@Override
			public void run() {
				//getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);			
			}	    	
	    });
	}
    
    /**
     * @param showInvPopup should we show the invitation popup?
     */
    private void displayInvitation(boolean showInvPopup) 
    {
        if (mIncomingInvitationId == null) {
            // no invitation, so no popup
            showInvPopup = false;
        }
    }
    
	@Override
	public void onRealTimeMessageReceived(RealTimeMessage arg0) {
		byte[] message = arg0.getMessageData();
		int messageType = message[0];
		if(mMoveReceiver != null) {
			switch(messageType)
			{
				case IMoveReceiver.MSG_GAME:	
					mMoveReceiver.receiveMove(message);
					break;
				case IMoveReceiver.MSG_READY:
					mOpponentReady = true;
					if(!mHosting && mMoveReceiver != null && mMoveReceiver.isReady())
					{
		    			byte[] msgStart = {IMoveReceiver.MSG_REQUEST_START};
		    			mPingTimer = new Date();
		    			broadcastMessage(msgStart);
					}
					break;
				case IMoveReceiver.MSG_REQUEST_START:
					mMoveReceiver.gameStart(5);
					byte[] msgStart = {IMoveReceiver.MSG_STARTING};
					broadcastMessage(msgStart);					
					break;
				case IMoveReceiver.MSG_STARTING:	
					Date now = new Date();
					float latency = (float)(now.getTime() - mPingTimer.getTime());
					float countdown = 5f - latency/2000;
					mMoveReceiver.gameStart(countdown);
					break;
				default:
					break;
			}
		}
	}
	
    @Override
    public int broadcastReliableMessage(byte[] message)  {
        // Send to other participant.
        if (mOpponent.getStatus() == Participant.STATUS_JOINED) {
            int result = Games.RealTimeMultiplayer.sendReliableMessage(mHelper.getApiClient(), this, message, mRoomId, mOpponent.getParticipantId()); 
            
            if(result == RealTimeMultiplayer.REAL_TIME_MESSAGE_FAILED) {
            	return -1;
            }
            return result;
        }
        return -1;
    }
    	
    // Broadcast game data to other player.
    @Override
    public void broadcastMessage(byte[] message)  {    	    	
    	if(mRoomId != null) {
	    	Games.RealTimeMultiplayer.sendUnreliableMessage(mHelper.getApiClient(), message, mRoomId, mOpponent.getParticipantId());        
    	}
    }    
    
    @Override
    public void broadcastReady()
    {
    	byte[] msgReady = {IMoveReceiver.MSG_READY};
		broadcastMessage(msgReady);
    	if(mOpponentReady)
    	{
    		if(!mHosting)
    		{
    			byte[] msgStart = {IMoveReceiver.MSG_REQUEST_START};
    			mPingTimer = new Date();
    			broadcastMessage(msgStart);
    		}
    	}
    }

    // Start the gameplay phase of the game.
    private void startGame(boolean multiplayer) 
    {
    	mGame.setMultiplayerScreen();
    }

	@Override
	public void startRandomMultiplayer() {
		if(!mConnected)
		{
			showLoadDialog();
			mConnected = true;
		    // auto-match criteria to invite one random automatch opponent.  
		    // You can also specify more opponents (up to 3). 
	        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
		    Bundle am = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS, MAX_OPPONENTS, 0);
	
		    // build the room config:
		    RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this)
		    		.setMessageReceivedListener(this)
		            .setRoomStatusUpdateListener(this)
		    		.setAutoMatchCriteria(am);
		    RoomConfig roomConfig = roomConfigBuilder.build();
	
		    // create room:
		    Games.RealTimeMultiplayer.create(mHelper.getApiClient(), roomConfig);
	
		    // prevent screen from sleeping during handshake
		    keepScreenOn();
		}
	}
	
	private void showLoadDialog()
	{
        runOnUiThread(new Runnable() {
                // @Override
                public void run() {
            		mLoadingDialog = ProgressDialog.show(mContext, "", "Loading. Please wait...", true);
                }
        });		
	}
	
	private void dismissLoadDialog()
	{
        runOnUiThread(new Runnable() {
                // @Override
                public void run() {
            		mLoadingDialog.dismiss();
                }
        });	
	}

	@Override
	public void registerMoveReceiver(IMoveReceiver receiver) {
		mMoveReceiver = receiver;
	}
    
    public void setupInterstitial()
    {
    	SnakeWarGame.AdController = new AndroidInterstitialAd();
    	// Create an ad.
    	mInterstitialAd = new InterstitialAd(this);
    	mInterstitialAd.setAdUnitId(AD_UNIT_ID);
    }
    
    public static void loadInterstitialAd() {
		// Check the logcat output for your hashed device ID to get test ads on a physical device.
		AdRequest adRequest = new AdRequest.Builder().build();
		
		// Load the interstitial ad.
		mInterstitialAd.loadAd(adRequest);
	}
    
    public static void showInterstitialAd() {
		// Disable the show button until another interstitial is loaded.
		if (mInterstitialAd.isLoaded()) 
		{
			mInterstitialAd.show();
		}
	}

	@Override
	public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
		if(statusCode == GamesStatusCodes.STATUS_REAL_TIME_MESSAGE_SEND_FAILED ) {
			//resend the message
			mMoveReceiver.sendFailed(tokenId);
		}
		else {
			mMoveReceiver.sendSuccess(tokenId);			
		}
	}

	@Override
	public void submitScore(int score) {
		Games.Leaderboards.submitScore(mHelper.getApiClient(), LEADERBOARD_ID, score);	
	}

	@Override
	public void disconnectGame() {
		leaveRoom();		
	}
	
	/**
	 * Checks if an internet connection is available by resolving a website IP 
	 * rather than just checking for a network connection which might not necessarily be 
	 * connected to the internet.
	 * @return
	 */
	@Override
	public boolean checkConnection() {
        try {
        	InetAddress ipAddr = InetAddress.getByName("google.com");
            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
	}
}