package im.delight.faceless;

/**
 * Copyright (C) 2014 www.delight.im <info@delight.im>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */

import im.delight.android.baselib.Data;
import im.delight.android.countries.Country;
import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

public class Message extends Content implements Parcelable {

	public static final int DEGREE_ADMIN = -2;
	public static final int DEGREE_EXAMPLE = -1;
	private final String mID;
	private final int mDegree;
	private final int mColor;
	private final int mPatternID;
	private final String mText;
	private final String mTopic;
	private int mFavorites;
	private int mComments;
	private final String mCountryISO3;
	private boolean mFavorited;
	private boolean mSubscribed;
	
	public Message(String id, int degree, String colorHex, int patternID, String text, String topic, long time, int favorites, int comments, String countryISO3) {
		mID = id;
		mDegree = degree;
		mColor = Color.parseColor(colorHex);
		mPatternID = patternID;
		mText = text;
		mTopic = topic;
		mTime = time;
		mFavorites = favorites;
		mComments = comments;
		mCountryISO3 = countryISO3;
		mFavorited = false;
		mSubscribed = false;
	}
	
	public String getID() {
		return mID;
	}
	
	public long getIDNumber() throws Exception {
		return getIDNumber(mID);
	}
	
	public static long getIDNumber(String idStr) throws Exception {
		if (idStr != null) {
			try {
				byte[] decimal = Base64.decode(idStr, Base64.NO_WRAP);
				return Long.parseLong(new String(decimal, "UTF-8"));
			}
			catch (Exception e) { }
		}
		throw new Exception("Cannot convert ID to number: "+idStr);
	}

	public int getDegree() {
		return mDegree;
	}
	
	public String getDegreeText(Context context) {
		if (mDegree == DEGREE_ADMIN) {
			return context.getString(R.string.app_name);
		}
		else if (mDegree == DEGREE_EXAMPLE) {
			return context.getString(R.string.degree_example);
		}
		else if (mDegree == 0) {
			return context.getString(R.string.degree_self);
		}
		else if (mDegree == 1) {
			return context.getString(R.string.degree_friend);
		}
		else if (mDegree == 2) {
			return context.getString(R.string.degree_friend_of_friend);
		}
		else {
			try {
				return getCountryName(context);
			}
			catch (Exception e) {
				return context.getString(R.string.degree_worldwide);
			}
		}
	}
	
	public boolean isAdminMessage() {
		return mDegree == DEGREE_ADMIN;
	}
	
	public boolean isExampleMessage() {
		return mDegree == DEGREE_EXAMPLE;
	}

	public int getColor() {
		return mColor;
	}

	public String getColorHex() {
		return Data.colorToHex(mColor);
	}

	public int getPatternID() {
		return mPatternID;
	}

	public String getText() {
		return mText;
	}
	
	public String getTopic() {
		return mTopic;
	}
	
	public String getTopicText(Context context) {
		if (mTopic == null) {
			return null;
		}
		else {
			if (mTopic.equals("meta")) {
				return context.getString(R.string.topics_meta_human);
			}
			else {
				final String topicResourceKey = "topic_"+mTopic;
				final int resID = context.getResources().getIdentifier(topicResourceKey, "string", context.getPackageName());
				if (resID > 0) {
					return context.getString(resID);
				}
				else {
					return mTopic;
				}
			}
		}
	}

	public int getFavorites() {
		return mFavorites;
	}
	
	public void updateFavorites(int delta) {
		mFavorites = mFavorites+delta;
	}

	public int getComments() {
		return mComments;
	}
	
	public void increaseComments() {
		mComments = mComments+1;
	}
	
	public String getCountryISO3() {
		return mCountryISO3;
	}
	
	public String getCountryName(Context context) throws Exception {
		return Country.getNameByIso3Code(context, mCountryISO3);
	}
	
	public boolean isFavorited() {
		return mFavorited;
	}
	
	public void setFavorited(boolean state) {
		mFavorited = state;
	}
	
	public boolean isSubscribed() {
		return mSubscribed;
	}
	
	public void setSubscribed(boolean state) {
		mSubscribed = state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mDegree;
		result = prime * result + ((mID == null) ? 0 : mID.hashCode());
		result = prime * result + ((mText == null) ? 0 : mText.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Message)) {
			return false;
		}
		Message other = (Message) obj;
		if (mDegree != other.mDegree) {
			return false;
		}
		if (mID == null) {
			if (other.mID != null) {
				return false;
			}
		}
		else if (!mID.equals(other.mID)) {
			return false;
		}
		if (mText == null) {
			if (other.mText != null) {
				return false;
			}
		}
		else if (!mText.equals(other.mText)) {
			return false;
		}
		return true;
	}

	public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
		@Override
		public Message createFromParcel(Parcel in) {
			return new Message(in);
		}
		@Override
		public Message[] newArray(int size) {
			return new Message[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mID);
		out.writeInt(mDegree);
		out.writeInt(mColor);
		out.writeInt(mPatternID);
		out.writeString(mText);
		out.writeString(mTopic);
		out.writeLong(mTime);
		out.writeInt(mFavorites);
		out.writeInt(mComments);
		out.writeString(mCountryISO3);
		out.writeByte((byte) (mFavorited ? 1 : 0));
		out.writeByte((byte) (mSubscribed ? 1 : 0));
	}

	private Message(Parcel in) {
		mID = in.readString();
		mDegree = in.readInt();
		mColor = in.readInt();
		mPatternID = in.readInt();
		mText = in.readString();
		mTopic = in.readString();
		mTime = in.readLong();
		mFavorites = in.readInt();
		mComments = in.readInt();
		mCountryISO3 = in.readString();
		mFavorited = in.readByte() == 1;
		mSubscribed = in.readByte() == 1;
	}

}