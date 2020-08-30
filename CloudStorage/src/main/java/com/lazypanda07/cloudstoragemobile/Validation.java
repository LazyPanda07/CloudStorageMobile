package com.lazypanda07.cloudstoragemobile;

import java.util.HashSet;

public class Validation
{
	private static HashSet<Character> init()
	{
		HashSet<Character> result = new HashSet<>();

		for (char i = 'A'; i <= 'Z'; i++)
		{
			result.add(i);
		}

		for (char i = 'a'; i <= 'z'; i++)
		{
			result.add(i);
		}

		for (char i = '0'; i <= '9'; i++)
		{
			result.add(i);
		}

		result.add('_');

		return result;
	}

	private static HashSet<Character> allowableCharacters = init();

	public static boolean validationUserData(String data)
	{
		for (int i = 0; i < data.length(); i++)
		{
			if (!allowableCharacters.contains(data.charAt(i)))
			{
				return false;
			}
		}

		return true;
	}
}
