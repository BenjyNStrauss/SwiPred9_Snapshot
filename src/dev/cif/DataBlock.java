package dev.cif;

import java.util.List;

import assist.translation.cplusplus.OStream;
import assist.translation.cplusplus.Vector;
import assist.util.Pair;

/*-
 * SPDX-License-Identifier: BSD-2-Clause
 *
 * Copyright (c) 2022 NKI/AVL, Netherlands Cancer Institute
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class DataBlock implements List<Category> {
	private String m_name;
	private final Validator m_validator = null;
	
	public DataBlock() { }

	/**
	 * @brief Construct a new datablock object with name @a name
	 * 
	 * @param name The name for the new datablock
	 */
	public DataBlock(String name) {
		m_name = name;
	}

	/** @cond */
	public DataBlock(final DataBlock db) {
		std::list<category>(db);
		
		m_name = db.m_name;
		m_validator = db.m_validator;

		for (auto cat : this) {
			cat.update_links(this);
		}
	}

	public DataBlock(DataBlock) = default;

	public datablock &operator=(const datablock &);
	public datablock &operator=(datablock &&) = default;
	/** @endcond */

	// --------------------------------------------------------------------

	/**
	 * @brief Return the name of this datablock
	 */
	public final String name() { return m_name; }

	/**
	 * @brief Set the name of this datablock to @a name
	 * 
	 * @param name The new name
	 */
	public void set_name(String name) {
		m_name = name;
	}

	/**
	 * @brief Set the validator object to @a v
	 * 
	 * @param v The new validator object, may be null
	 */
	public void set_validator(final validator v);

	/**
	 * @brief Get the validator object
	 * 
	 * @return const validator* The validator or nullptr if there is none
	 */
	public final Validator get_validator() ;

	/**
	 * @brief Validates the content of this datablock and all its content
	 * 
	 * @return true If the content is valid
	 * @return false If the content is not valid
	 */
	public boolean is_valid() {
		if (m_validator == null) {
			throw new RuntimeException("Validator not specified");
		}

		boolean result = true;
		for (auto cat : this) {
			result = cat.is_valid() && result;
		}

		return result;
	}

	/**
	 * @brief Validates the content of this datablock and all its content
	 * and updates or removes the audit_conform category to match the result.
	 * 
	 * @return true If the content is valid
	 * @return false If the content is not valid
	 */
	public boolean is_valid1() {
		if (m_validator == null) {
			throw new RuntimeException("Validator not specified");
		}

		boolean result = true;
		for (auto cat : this) {
			result = cat.is_valid() && result;
		}
		
		// Add or remove the audit_conform block here.
		if (result) {
			// If the dictionary declares an audit_conform category, put it in,
			// but only if it does not exist already!

			if (m_validator.get_validator_for_category("audit_conform") != null) {
				auto audit_conform = operator_brackets("audit_conform");

				audit_conform.clear();
				audit_conform.emplace({
					// clang-format off
					{ "dict_name", m_validator->name() },
					{ "dict_version", m_validator->version() }
					// clang-format on
				});
			}
		}
		else {
			erase(std::find_if(begin(), end(), [](category &cat) { return cat.name() == "audit_conform"; }), end());
		}
		
		return result;
	}

	/**
	 * @brief Validates all contained data for valid links between parents and children
	 * as defined in the validator
	 * 
	 * @return true If all links are valid
	 * @return false If all links are not valid
	 */
	public boolean validate_links();

	// --------------------------------------------------------------------

	/**
	 * @brief Return the category named @a name, will create a new and empty
	 * category named @a name if it does not exist.
	 * 
	 * @param name The name of the category to return
	 * @return category& Reference to the named category
	 */
	public Category operator_brackets(String name);

	/**
	 * @brief Return the const category named @a name, will return a reference
	 * to a static empty category if it was not found.
	 * 
	 * @param name The name of the category to return
	 * @return category& Reference to the named category
	 */
	public final Category operator_brackets1(String name);

	/**
	 * @brief Return a pointer to the category named @a name or nullptr if
	 * it does not exist.
	 * 
	 * @param name The name of the category
	 * @return category* Pointer to the category found or nullptr
	 */
	public Category get(String name);

	/**
	 * @brief Return a pointer to the category named @a name or nullptr if
	 * it does not exist.
	 * 
	 * @param name The name of the category
	 * @return category* Pointer to the category found or nullptr
	 */
	public final Category get1(String name);

	/**
	 * @brief Tries to find a category with name @a name and will create a
	 * new one if it is not found. The result is a tuple of an iterator
	 * pointing to the category and a boolean indicating whether the category
	 * was created or not.
	 * 
	 * @param name The name for the category
	 * @return std::tuple<iterator, bool> A tuple containing an iterator pointing
	 * at the category and a boolean indicating whether the category was newly
	 * created.
	 */
	public Pair<Iterator, Boolean> emplace(String name);

	/**
	 * @brief Get the preferred order of the categories when writing them
	 */
	@Deprecated//("use get_item_order instead")
	public Vector<String> get_tag_order() {
		return get_item_order();
	}

	/**
	 * @brief Get the preferred order of the categories when writing them
	 */
	public Vector<String> get_item_order();

	/**
	 * @brief Write out the contents to @a os
	 */
	public void write(OStream os) ;

	/**
	 * @brief Write out the contents to @a os using the order defined in @a item_name_order
	 */
	public void write(OStream os, final Vector<String> item_name_order);

	/**
	 * @brief Friend operator<< to write datablock @a db to std::ostream @a os
	 */
	OStream operator_ltlt(OStream os, final DataBlock db) {
		db.write(os);
		return os;
	}

	// --------------------------------------------------------------------

	/**
	 * @brief Comparison operator to compare two datablock for equal content
	 */
	boolean operator==(final datablock rhs);


}
