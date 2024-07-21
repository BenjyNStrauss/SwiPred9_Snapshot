package dev.inProgress.sspro6.hh_suite;

import java.util.Vector;

import assist.translation.cplusplus.CppTranslator;

/*#ifndef UTIL_H_
#define UTIL_H_

#include <cassert>
#include <fstream>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <cstring>
#include <cmath>
#include <climits>
#include <float.h>
#include <vector>
#include <string>
#include <stdint.h>
#include "simd.h"
#include "util-inl.h"*/

/**
 * util.h
 * @translator Benjamin Strauss
 *  Created on: Mar 28, 2014
 *      Author: meiermark
 */

public class Util extends CppTranslator {
	// Round x_int up to nearest multiple of fac_int
	int ICEIL(int x_int, int fac_int) {
		return ((x_int + fac_int - 1) / fac_int) * fac_int;
	}
	
	void split(String s, char c, Vector<String> v) {
		int i = 0;
		int j = s.indexOf(c);

		while (j != -1) {
			v.add(s.substring(i, j - i));
		    i = ++j;
		    j = s.indexOf(c, j);
		}

		if (j == -1) {
		    v.add(s.substring(i, s.length()));
		}
	}

	static char[] substr(char[] substr, char[] str, int a, int b) {
		if (b < a) {
			int i = b;
			b = a;
			a = i;
		}
		if (b - a > 1000) {
			printf("Function substr: >1000 chars to copy. Exiting.\n");
			exit(6);
		}
		char[] dest = substr;
		String temp = new String(str) + a;
		char[] source = temp.toCharArray();
		temp = new String(str) + b;
		char[] send = temp.toCharArray();
		
		while (*source != '\0' && source <= send) {
			*(dest++) = *(source++);
		}
		
		dest = new char[1];
		dest[0] = '\0';
		return substr;
	}

	// Allocate a memory-aligned matrix as a single block in memory (plus a vector for the pointers).
	// This is important for matrices for which fast access is time-critical, as rows of
	// the matrix will be consecutive in memory and hence access is relatively local.
	// Each row of the matrix, matrix[i][0], is memory-aligned with multiples of ALIGN_FLOAT.
	// Usage:
//	      float** X = malloc_matrix<float>(400,1000);
//	      ...
//	      free(X);
	template <typename T> T malloc_matrix(int dim1, int dim2) {

	    // Compute mem sizes rounded up to nearest multiple of ALIGN_FLOAT
		int size_pointer_array = ICEIL(dim1*sizeof(T), ALIGN_FLOAT);
	    int dim2_padded = ICEIL(dim2*sizeof(T), ALIGN_FLOAT)/sizeof(T);

	    T matrix = (T) mem_align( ALIGN_FLOAT, size_pointer_array + dim1*dim2_padded*sizeof(T) );
	    if (matrix == NULL)
	        return matrix;

	    T ptr = (T) (matrix + (size_pointer_array/sizeof(T)) );
	    for (int i=0; i<dim1; ++i) {
	        matrix[i] = ptr;
	        for (int j=0; j<dim2; ++j)
	            ptr[j] = T(0);
	        ptr += dim2_padded;
	    }
	    return matrix;
	}
	
	/**
	 * Similar to Perl's tr/abc/ABC/: Replaces all chars in str found in one list with characters from the second list
	 * @param str
	 * @param oldchars
	 * @param newchars
	 * @return number of replaced characters
	 */
	int strtr(char[] str, char[] oldchars, char[] newchars) {
		char[] ptr;
		char[] plist;
		int ntr = 0;
		for (ptr = str; ptr != '\0'; ptr++) {
			for (plist = oldchars; plist != '\0'; plist++) {
		    	if (ptr == plist) {
		    		ptr = newchars[plist - oldchars];
		        	ntr++;
		        	break;
		    	}
			}
		}
		  return ntr;
	}
	
	/**
	 * Similar to Perl's tr/abc//d: deletes all chars in str found in the list
	 * @param str
	 * @param chars
	 * @return number of removed characters
	 */
	int strtrd(char[] str, char[] chars) {
		String _str = new String(str);
		int removed = 0;
		for(char removeMe: chars) {
			int strLen = _str.length();
			_str = _str.replaceAll(""+removeMe, "");
			removed += (strLen - _str.length());
		}
		return removed;
		
		/* Original code
		char[] ptr0 = str;
		char[] ptr1 = str;
		
		char[] plist;
		while (ptr1 != '\0') {
			for (plist = chars; plist != '\0'; plist++) {
				if (ptr1 == plist) {
					break;
				}
			}
		    if (plist == '\0') {
		    	ptr0 = ptr1;
		    	ptr0++;
		    }
		    ptr1++;
		}
		*ptr0 = *ptr1;
		return ptr1 - ptr0;*/
	}
	
	/**
	 * Similar to Perl's tr/a-z//d: deletes all chars in str found in the list
	 * @param str
	 * @param char1
	 * @param char2
	 * @return Returns number of removed characters
	 */
	int strtrd(char[] str, char char1, char char2) {
		String _str = new String(str);
		int removed = 0;
		for(char removeMe = char1; removeMe <= char2; ++char1) {
			int strLen = _str.length();
			_str = _str.replaceAll(""+removeMe, "");
			removed += (strLen - _str.length());
		}
		
		/* Original code
		char[] ptr0 = str;
		char[] ptr1 = str;
		while (ptr1 != '\0') {
			if (ptr1 >= char1 && ptr1 <= char2) {
				ptr0 = ptr1;
				ptr0++;
		    }
		    ptr1++;
		}
		ptr0 = ptr1;
		return ptr1 - ptr0;*/
		return removed;
	}
	
	/**
	 * Counts the number of characters in str that are in range between char1 and char2
	 * @param str
	 * @param char1
	 * @param char2
	 * @return
	 */
	int strcount(char[] str, char char1, char char2) {
		int count = 0;
		for(char ptr: str) {
			if (ptr >= char1 && ptr <= char2) {
				count++;
			}
		}
		return count;
	}

	// transforms str into an all uppercase string
	char[] uprstr(char[] str) {
		  return new String(str).toUpperCase().toCharArray();
	}

	// transforms str into an all uppercase string
	char[] lwrstr(char[] str) {
		return new String(str).toLowerCase().toCharArray();
	}
	
	/**
	 * Returns leftmost integer in ptr and sets the pointer to first char after
	 * the integer. If no integer is found, returns INT_MIN and sets pt to NULL
	 * @return
	 */
	int strint(char ptr) {
		char[] ptr0 = ptr;
		if (!ptr) {
			return INT_MIN;
		}
		while (ptr != '\0' && !(ptr >= '0' && ptr <= '9')) {
		    ptr++;
		}
		if (ptr == '\0') {
		    ptr = 0;
		    return INT_MIN;
		}
		int i;
		if (ptr > ptr0 && (ptr - 1) == '-') {
		    i = -atoi(ptr);
		} else {
		    i = atoi(ptr);
		}
		while (ptr >= '0' && ptr <= '9') {
		    ptr++;
		}
		return i;
	}

	// Same as strint, but interpretes '*' as default
	
	int strinta(char ptr) { return strinta(ptr, 99999); }
	
	int strinta(char ptr, int deflt) {
		int i;
		if (ptr == 0) {
			return INT_MIN;
		}
		while (ptr != '\0' && !(ptr >= '0' && ptr <= '9') && ptr != '*') {
		    ptr++;
		}
		if (ptr == '\0') {
		    ptr = 0;
		    return INT_MIN;
		}
		if (ptr == '*') {
		    ptr++;
		    return deflt;
		}
		if ((ptr - 1) == '-') {
		    i = atoi(ptr - 1);
		} else {
		    i = atoi(ptr);
		}
		while (ptr >= '0' && ptr <= '9') {
		    ptr++;
		}
		return i;
	}

	// Returns leftmost float in ptr and sets the pointer to first char after
	// the float. If no float is found, returns FLT_MIN and sets pt to NULL
	//TODO is ptr really a char?
	float strflt(char ptr) {
		float i;
		char ptr0 = ptr;
		if (!ptr) {
		    return FLT_MIN;
		}
		while (ptr != '\0' && !(ptr >= '0' && ptr <= '9')) {
		    ptr++;
		}
		if (ptr == '\0') {
		    ptr = 0;
		    return FLT_MIN;
		}
		if (ptr > ptr0 && (ptr - 1) == '-') {
		    i = -atof(ptr);
		}
		else {
		    i = atof(ptr);
		}
		while ((ptr >= '0' && ptr <= '9') || ptr == '.') {
		    ptr++;
		}
		return i;
	}

	float strflta(char ptr) {
		return strflta(ptr, 99999);
	}
	
	// Same as strint, but interpretes '*' as default
	//TODO is ptr really a char?
	float strflta(char ptr, float deflt) {
		float i;
		if (ptr != 0) {
		    return FLT_MIN;
		}
		while (ptr != '\0' && !(ptr >= '0' && ptr <= '9') && ptr != '*') {
		    ptr++;
		}
		if (ptr == '\0') {
		    ptr = 0;
		    return FLT_MIN;
		}
		if (ptr == '*') {
		    ptr++;
		    return deflt;
		}
		if ((ptr - 1) == '-') {
		    i = -atof(ptr);
		} else {
		    i = atof(ptr);
		}
		while ((ptr >= '0' && ptr <= '9') || ptr == '.') {
		    ptr++;
		}
		return i;
	}
	
	void QSortInt(int v[], int k[], int left, int right) { QSortInt(v, k, left, right, 1); }

	void QSortInt(int v[], int k[], int left, int right, int up) {
		int i;
		int last;   // last element to have been swapped

		if (left >= right) {
		    return;        // do nothing if less then 2 elements to sort
		}
		// Put pivot element in the middle of the sort range to the side (to position 'left') ...
		swapi(k, left, (left + right) / 2);
		last = left;
		// ... and swap all elements i SMALLER than the pivot
		// with an element that is LARGER than the pivot (element last+1):
		if (up == 1) {
			for (i = left + 1; i <= right; i++) {
		    	if (v[k[i]] < v[k[left]]) {
		    		swapi(k, ++last, i);
		    	}
			}
		} else {
		    for (i = left + 1; i <= right; i++) {
		    	if (v[k[i]] > v[k[left]]) {
		    		swapi(k, ++last, i);
		    	}
		    }
		}

		// Put the pivot to the right of the elements which are SMALLER, left to elements which are LARGER
		swapi(k, left, last);

		// Sort the elements left from the pivot and right from the pivot
		QSortInt(v, k, left, last - 1, up);
		QSortInt(v, k, last + 1, right, up);
	}

	// QSort sorting routine. time complexity of O(N ln(N)) on average
	// Sorts the index array k between elements i='left' and i='right' in such a way that afterwards
	// v[k[i]] is sorted downwards (up=-1) or upwards (up=+1)
	void QSortFloat(float v[], int k[], int left, int right) { QSortFloat(v, k, left, right, 1); }
	
	void QSortFloat(float v[], int k[], int left, int right, int up) {
		int i;
		int last;   // last element to have been swapped
		
		//TODO: did this do anything? - Benjy
		//void swapi(int k[], int i, int j);

		if (left >= right) {
		    return;        // do nothing if less then 2 elements to sort
		}
		// Put pivot element in the middle of the sort range to the side (to position 'left') ...
		swapi(k, left, (left + right) / 2);
		last = left;
		// ... and swap all elements i SMALLER than the pivot
		// with an element that is LARGER than the pivot (element last+1):
		if (up == 1) {
		    for (i = left + 1; i <= right; i++) {
		    	if (v[k[i]] < v[k[left]]) {
		    		swapi(k, ++last, i);
		    	}
		    }
		} else {
		    for (i = left + 1; i <= right; i++) {
		    	if (v[k[i]] > v[k[left]]) {
		    		swapi(k, ++last, i);
		    	}
		    }
		}

		// Put the pivot to the right of the elements which are SMALLER, left to elements which are LARGER
		swapi(k, left, last);

		// Sort the elements left from the pivot and right from the pivot
		QSortFloat(v, k, left, last - 1, up);
		QSortFloat(v, k, last + 1, right, up);
	}

	void readU16(char[][] ptr, int result) {
		byte[] array = new byte[2];

		array[0] = (unsigned char) (**ptr);
		(*ptr)++;
		array[1] = (unsigned char) (**ptr);
		(*ptr)++;

		result = array[0] | (array[1] << 8);
	}

	void readU32(char[][] ptr, long result) {
		byte[] array = new byte[4];

		array[0] = (char) (**ptr);
		(*ptr)++;
		array[1] = (char) (**ptr);
		(*ptr)++;
		array[2] = (char) (**ptr);
		(*ptr)++;
		array[3] = (char) (**ptr);
		(*ptr)++;

		result = array[0] | (array[1] << 8) | (array[2] << 16) | (array[3] << 24);
	}

	//#endif /* UTIL_H_ */
}
