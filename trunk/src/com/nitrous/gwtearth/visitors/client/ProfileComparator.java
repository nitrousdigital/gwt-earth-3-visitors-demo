package com.nitrous.gwtearth.visitors.client;

import java.util.Comparator;

import com.nitrous.gwtearth.visitors.shared.AccountProfile;

public class ProfileComparator implements Comparator<AccountProfile> {

	@Override
	public int compare(AccountProfile o1, AccountProfile o2) {
		if (o1 == null && o2 == null) {
			return 0;
		}
		if (o1 == null && o2 != null) {
			return 1;
		} else if (o2 == null && o1 != null) {
			return -1;
		} else {
			return o1.getProfileName().compareToIgnoreCase(o2.getProfileName());
		}
	}

}

