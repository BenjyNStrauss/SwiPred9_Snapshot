package dev.cif;

import java.util.Set;

import assist.Deconstructable;
import assist.exceptions.ValueOutOfRangeException;
import assist.translation.cplusplus.OStream;
import assist.translation.cplusplus.Vector;

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

//friend class row_handle;

/*
 * 			template <typename, typename...>
			friend class iterator_impl;

			using value_type = row_handle;
			using reference = value_type;
			using const_reference = const value_type;
			using iterator = iterator_impl<category>;
			using const_iterator = iterator_impl<const category>;
 */

public class Category implements Deconstructable {
	
	private String m_name;
	private Vector<ItemEntry> m_items;
	private Validator m_validator = null;
	private category_validator m_cat_validator = null;
	private Vector<link> m_parent_links, m_child_links;
	private boolean m_cascade = true;
	private int m_last_unique_num = 0;
	class category_index *m_index = nullptr;
	private row m_head = null, m_tail = null;
	
	public Category() { }
	
	public Category(String name);          ///< Constructor taking a \a name
	
	public Category(final category &rhs);            ///< Copy constructor
	public Category(category &&rhs);                 ///< Move constructor
	public Category &operator=(const category &rhs); ///< Copy assignement operator
	public Category &operator=(category &&rhs);      ///< Move assignement operator

	/// @brief Destructor
	/// @note Please note that the destructor is not virtual. It is assumed that
	/// you will not derive from this class.
	~category();

	// --------------------------------------------------------------------

	public String name() { return m_name; } ///< Returns the name of the category

	@Deprecated//("use key_items instead")]]
	public iset key_fields();                           ///< Returns the cif::iset of key item names. Retrieved from the @ref category_validator for this category

	public iset key_items();                           ///< Returns the cif::iset of key item names. Retrieved from the @ref category_validator for this category

	@Deprecated//("use key_item_indices instead")]]
	public Set<Short> key_field_indices();      ///< Returns a set of indices for the key items.

	public Set<Short> key_item_indices();      ///< Returns a set of indices for the key items.

	/// @brief Set the validator for this category to @a v
	/// @param v The category_validator to assign. A nullptr value is allowed.
	/// @param db The enclosing @ref DataBlock
	public void set_validator(final Validator v, DataBlock db);

	/// @brief Update the links in this category
	/// @param db The enclosing @ref DataBlock
	public void update_links(DataBlock db);

	/// @brief Return the global @ref validator for the data
	/// @return The @ref validator or nullptr if not assigned
	public final Validator get_validator() { return m_validator; }

	/// @brief Return the category validator for this category
	/// @return The @ref category_validator or nullptr if not assigned
	public final category_validator get_cat_validator() { return m_cat_validator; }

	/// @brief Validate the data stored using the assigned @ref category_validator
	/// @return Returns true is all validations pass
	boolean is_valid();

	/// @brief Validate links, that means, values in this category should have an
	/// accompanying value in parent categories.
	///
	/// @note
	/// The code makes one exception when validating missing links and that's between
	/// *atom_site* and a parent *pdbx_poly_seq_scheme* or *entity_poly_seq*.
	/// This particular case should be skipped because it is wrong:
	/// there are atoms that are not part of a polymer, and thus will have no
	/// parent in those categories.
	///
	/// @return Returns true is all validations pass
	public boolean validate_links();

	/// @brief Equality operator, returns true if @a rhs is equal to this
	/// @param rhs The object to compare with
	/// @return True if the data contained is equal
	boolean operator==(const category rhs) const;

	// --------------------------------------------------------------------

	/// @brief Return a reference to the first row in this category.
	/// @return Reference to the first row in this category. The result is undefined if
	/// the category is empty.
	public reference front() {
		return { this, m_head };
	}

	/// @brief Return a const reference to the first row in this category.
	/// @return const reference to the first row in this category. The result is undefined if
	/// the category is empty.
	public const_reference front() {
		return { const_cast<category>(this), const_cast<row >(m_head) };
	}

	/// @brief Return a reference to the last row in this category.
	/// @return Reference to the last row in this category. The result is undefined if
	/// the category is empty.
	public reference back() {
		return { this, m_tail };
	}

	/// @brief Return a const reference to the last row in this category.
	/// @return const reference to the last row in this category. The result is undefined if
	/// the category is empty.
	public const_reference back() {
		return { const_cast<category>(this), const_cast<row>(m_tail) };
	}

	/// Return an iterator to the first row
	public iterator begin() {
		return { this, m_head };
	}

	/// Return an iterator pointing past the last row
	public iterator end() {
		return { this, null };
	}

	/// Return a const iterator to the first row
	public const_iterator begin() {
		return { this, m_head };
	}

	/// Return a const iterator pointing past the last row
	public const_iterator end() {
		return { this, null };
	}

	/// Return a const iterator to the first row
	public const_iterator cbegin() {
		return { this, m_head };
	}

	/// Return an iterator pointing past the last row
	public const_iterator cend() {
		return { this, null };
	}

	/// Return a count of the rows in this container
	public size_t size() {
		return std::distance(cbegin(), cend());
	}

	/// Return the theoretical maximum number or rows that can be stored
	public size_t max_size() {
		return std::numeric_limits<size_t>::max(); // this is a bit optimistic, I guess
	}

	/// Return true if the category is empty
	public boolean empty() {
		return m_head == null;
	}

	// --------------------------------------------------------------------
	// A category can have a key, as defined by the validator/dictionary

	/// @brief The key type
	//using key_type = row_initializer;

	/// @brief Return a row_handle for the row specified by \a key
	/// @param key The value for the key, items specified in the dictionary should have a value
	/// @return The row found in the index, or an undefined row_handle
	public row_handle operator_brackets(final row_initializer key);

	/// @brief Return a const row_handle for the row specified by \a key
	/// @param key The value for the key, items specified in the dictionary should have a value
	/// @return The row found in the index, or an undefined row_handle
	public final row_handle operator_brackets(final row_initializer key) {
		return const_cast<Category>(this).operator_brackets(key);
	}

	// --------------------------------------------------------------------

	/// @brief Return a special const iterator for all rows in this category.
	/// This iterator can be used in a structured binding context. E.g.:
	///
	/// @code{.cpp}
	/// for (const auto &[name, value] : cat.rows<String,int>("item_name", "item_value"))
	///   std::cout << name << ": " << value << '\n';
	/// @endcode
	///
	/// @tparam Ts The types for the items requested
	/// @param names The names for the items requested

	template <typename... Ts, typename... Ns>
	iterator_proxy<final Category, Ts...> rows(Ns... names) {
		static_assert(sizeof...(Ts) == sizeof...(Ns), "The number of item names should be equal to the number of types to return");
		return iterator_proxy<final Category, Ts...>(this, begin(), { names... });
	}

	/// @brief Return a special iterator for all rows in this category.
	/// This iterator can be used in a structured binding context. E.g.:
	///
	/// @code{.cpp}
	/// for (const auto &[name, value] : cat.rows<String,int>("item_name", "item_value"))
	///   std::cout << name << ": " << value << '\n';
	///
	/// // or in case we only need one item:
	///
	/// for (int id : cat.rows<int>("id"))
	///   std::cout << id << '\n';
	/// @endcode
	///
	/// @tparam Ts The types for the items requested
	/// @param names The names for the items requested

	template <typename... Ts, typename... Ns>
	iterator_proxy<category, Ts...> rows(Ns... names)
	{
		static_assert(sizeof...(Ts) == sizeof...(Ns), "The number of item names should be equal to the number of types to return");
		return iterator_proxy<category, Ts...>(*this, begin(), { names... });
	}

	// --------------------------------------------------------------------

	/// @brief Return a special iterator to loop over all rows that conform to @a cond
	///
	/// @code{.cpp}
	/// for (row_handle rh : cat.find(cif::key("first_name") == "John" and cif::key("last_name") == "Doe"))
	///    .. // do something with rh
	/// @endcode
	///
	/// @param cond The condition for the query
	/// @return A special iterator that loops over all elements that match. The iterator can be dereferenced
	/// to a @ref row_handle

	public conditional_iterator_proxy<category> find(condition cond) {
		return find(begin(), std::move(cond));
	}

	/// @brief Return a special iterator to loop over all rows that conform to @a cond
	/// starting at @a pos
	///
	/// @param pos Where to start searching
	/// @param cond The condition for the query
	/// @return A special iterator that loops over all elements that match. The iterator can be dereferenced
	/// to a @ref row_handle

	public conditional_iterator_proxy<category> find(iterator pos, condition cond) {
		return { this, pos, std::move(cond) };
	}

	/// @brief Return a special const iterator to loop over all rows that conform to @a cond
	///
	/// @param cond The condition for the query
	/// @return A special iterator that loops over all elements that match. The iterator can be dereferenced
	/// to a const @ref row_handle

	public conditional_iterator_proxy<category> find(condition cond) {
		return find(cbegin(), std::move(cond));
	}

	/// @brief Return a special const iterator to loop over all rows that conform to @a cond
	/// starting at @a pos
	///
	/// @param pos Where to start searching
	/// @param cond The condition for the query
	/// @return A special iterator that loops over all elements that match. The iterator can be dereferenced
	/// to a const @ref row_handle

	conditional_iterator_proxy<const category> find(const_iterator pos, condition &&cond) const
	{
		return conditional_iterator_proxy<const category>{ *this, pos, std::move(cond) };
	}

	/// @brief Return a special iterator to loop over all rows that conform to @a cond. The resulting
	/// iterator can be used in a structured binding context.
	///
	/// @code{.cpp}
	/// for (const auto &[name, value] : cat.find<String,int>(cif::key("item_value") > 10, "item_name", "item_value"))
	///    std::cout << name << ": " << value << '\n';
	/// @endcode
	///
	/// @param cond The condition for the query
	/// @tparam Ts The types for the items requested
	/// @param names The names for the items requested
	/// @return A special iterator that loops over all elements that match.

	template <typename... Ts, typename... Ns>
	conditional_iterator_proxy<category, Ts...> find(condition &&cond, Ns... names)
	{
		static_assert(sizeof...(Ts) == sizeof...(Ns), "The number of item names should be equal to the number of types to return");
		return find<Ts...>(cbegin(), std::move(cond), std::forward<Ns>(names)...);
	}

	/// @brief Return a special const iterator to loop over all rows that conform to @a cond. The resulting
	/// iterator can be used in a structured binding context.
	///
	/// @param cond The condition for the query
	/// @tparam Ts The types for the items requested
	/// @param names The names for the items requested
	/// @return A special iterator that loops over all elements that match.

	template <typename... Ts, typename... Ns>
	conditional_iterator_proxy<const category, Ts...> find(condition &&cond, Ns... names) const
	{
		static_assert(sizeof...(Ts) == sizeof...(Ns), "The number of item names should be equal to the number of types to return");
		return find<Ts...>(cbegin(), std::move(cond), std::forward<Ns>(names)...);
	}

	/// @brief Return a special iterator to loop over all rows that conform to @a cond starting at @a pos.
	/// The resulting iterator can be used in a structured binding context.
	///
	/// @param pos Iterator pointing to the location where to start
	/// @param cond The condition for the query
	/// @tparam Ts The types for the items requested
	/// @param names The names for the items requested
	/// @return A special iterator that loops over all elements that match.

	template <typename... Ts, typename... Ns>
	conditional_iterator_proxy<category, Ts...> find(const_iterator pos, condition &&cond, Ns... names)
	{
		static_assert(sizeof...(Ts) == sizeof...(Ns), "The number of item names should be equal to the number of types to return");
		return { this, pos, std::move(cond), std::forward<Ns>(names)... };
	}

	/// @brief Return a special const iterator to loop over all rows that conform to @a cond starting at @a pos.
	/// The resulting iterator can be used in a structured binding context.
	///
	/// @param pos Iterator pointing to the location where to start
	/// @param cond The condition for the query
	/// @tparam Ts The types for the items requested
	/// @param names The names for the items requested
	/// @return A special iterator that loops over all elements that match.

	template <typename... Ts, typename... Ns>
	conditional_iterator_proxy<const category, Ts...> find(const_iterator pos, condition &&cond, Ns... names) const
	{
		static_assert(sizeof...(Ts) == sizeof...(Ns), "The number of item names should be equal to the number of types to return");
		return { this, pos, std::move(cond), std::forward<Ns>(names)... };
	}

	// --------------------------------------------------------------------
	// if you only expect a single row

	/// @brief Return the row handle for the row that matches @a cond Throws @a multiple_results_error if
	/// there are is not exactly one row matching @a cond
	/// @param cond The condition to search for
	/// @return Row handle to the row found
	public row_handle find1(condition cond) {
		return find1(begin(), std::move(cond));
	}

	/// @brief Return the row handle for the row that matches @a cond starting at @a pos
	/// Throws @a multiple_results_error if there are is not exactly one row matching @a cond
	/// @param pos The position to start the search
	/// @param cond The condition to search for
	/// @return Row handle to the row found
	public row_handle find1(iterator pos, condition cond) {
		auto h = find(pos, std::move(cond));

		if (h.size() != 1) {
			throw multiple_results_error();
		}

		return h.begin();
	}

	/// @brief Return the const row handle for the row that matches @a cond Throws @a multiple_results_error if
	/// there are is not exactly one row matching @a cond
	/// @param cond The condition to search for
	/// @return Row handle to the row found
	public row_handle find1(condition cond) {
		return find1(cbegin(), std::move(cond));
	}

	/// @brief Return const the row handle for the row that matches @a cond starting at @a pos
	/// Throws @a multiple_results_error if there are is not exactly one row matching @a cond
	/// @param pos The position to start the search
	/// @param cond The condition to search for
	/// @return Row handle to the row found
	public row_handle find1(const_iterator pos, condition cond) {
		auto h = find(pos, std::move(cond));

		if (h.size() != 1) {
			throw multiple_results_error();
		}
		
		return h.begin();
	}

	/// @brief Return value for the item named @a item for the single row that
	/// matches @a cond. Throws @a multiple_results_error if there are is not exactly one row
	/// @tparam The type to use for the result
	/// @param cond The condition to search for
	/// @param item The name of the item to return the value for
	/// @return The value found
	template <typename T>
	T find1(condition &&cond, String item) {
		return find1<T>(cbegin(), std::move(cond), item);
	}

	/// @brief Return value for the item named @a item for the single row that
	/// matches @a cond when starting to search at @a pos.
	/// Throws @a multiple_results_error if there are is not exactly one row
	/// @tparam The type to use for the result
	/// @param pos The location to start the search
	/// @param cond The condition to search for
	/// @param item The name of the item to return the value for
	/// @return The value found
	template <typename T, std::enable_if_t<not is_optional_v<T>, int> = 0>
	T find1(const_iterator pos, condition &&cond, String item) {
		auto h = find<T>(pos, std::move(cond), item);

		if (h.size() != 1) {
			throw multiple_results_error();
		}

		return h.begin();
	}

	/// @brief Return a value of type std::optional<T> for the item named @a item for the single row that
	/// matches @a cond when starting to search at @a pos.
	/// If the row was not found, an empty value is returned.
	/// @tparam The type to use for the result
	/// @param pos The location to start the search
	/// @param cond The condition to search for
	/// @param item The name of the item to return the value for
	/// @return The value found, can be empty if no row matches the condition
	template <typename T, std::enable_if_t<is_optional_v<T>, int> = 0>
	T find1(const_iterator pos, condition cond, String item) {
		auto h = find<typename T::value_type>(pos, std::move(cond), item);

		if (h.size() > 1) {
			throw multiple_results_error();
		}

		if (h.empty()) {
			return {};
		}

		return h.begin();
	}

	/// @brief Return a std::tuple for the values for the items named in @a items
	/// for the single row that matches @a cond
	/// Throws @a multiple_results_error if there are is not exactly one row
	/// @tparam The types to use for the resulting tuple
	/// @param cond The condition to search for
	/// @param items The names of the items to return the value for
	/// @return The values found as a single tuple of type std::tuple<Ts...>
	template <typename... Ts, typename... Cs, typename U = std::enable_if_t<sizeof...(Ts) != 1>>
	std::tuple<Ts...> find1(condition &&cond, Cs... items) {
		static_assert(sizeof...(Ts) == sizeof...(Cs), "The number of item names should be equal to the number of types to return");
		// static_assert(std::is_same_v<Cs, const char*>..., "The item names should be const char");
		return find1<Ts...>(cbegin(), std::move(cond), std::forward<Cs>(items)...);
	}

	/// @brief Return a std::tuple for the values for the items named in @a items
	/// for the single row that matches @a cond when starting to search at @a pos
	/// Throws @a multiple_results_error if there are is not exactly one row
	/// @tparam The types to use for the resulting tuple
	/// @param pos The location to start the search
	/// @param cond The condition to search for
	/// @param items The names of the items to return the value for
	/// @return The values found as a single tuple of type std::tuple<Ts...>
	template <typename... Ts, typename... Cs, typename U = std::enable_if_t<sizeof...(Ts) != 1>>
	std::tuple<Ts...> find1(const_iterator pos, condition &&cond, Cs... items) {
		static_assert(sizeof...(Ts) == sizeof...(Cs), "The number of item names should be equal to the number of types to return");
		auto h = find<Ts...>(pos, std::move(cond), std::forward<Cs>(items)...);

		if (h.size() != 1)
			throw multiple_results_error();

		return *h.begin();
	}

	// --------------------------------------------------------------------
	// if you want only a first hit

	/// @brief Return a row handle to the first row that matches @a cond
	/// @param cond The condition to search for
	/// @return The handle to the row that matches or an empty row_handle
	public row_handle find_first(condition cond) {
		return find_first(begin(), std::move(cond));
	}

	/// @brief Return a row handle to the first row that matches @a cond starting at @a pos
	/// @param pos The location to start searching
	/// @param cond The condition to search for
	/// @return The handle to the row that matches or an empty row_handle
	public row_handle find_first(iterator pos, condition cond) {
		auto h = find(pos, std::move(cond));

		return h.empty() ? row_handle{} : *h.begin();
	}

	/// @brief Return a const row handle to the first row that matches @a cond
	/// @param cond The condition to search for
	/// @return The const handle to the row that matches or an empty row_handle
	public final row_handle find_first(condition cond) {
		return find_first(cbegin(), std::move(cond));
	}

	/// @brief Return a const row handle to the first row that matches @a cond starting at @a pos
	/// @param pos The location to start searching
	/// @param cond The condition to search for
	/// @return The const handle to the row that matches or an empty row_handle
	public final row_handle find_first(const_iterator pos, condition cond) {
		auto h = find(pos, std::move(cond));

		return h.empty() ? row_handle{} : h.begin();
	}

	/// @brief Return the value for item @a item for the first row that matches condition @a cond
	/// @tparam The type of the value to return
	/// @param cond The condition to search for
	/// @param item The item for which the value should be returned
	/// @return The value found or a default constructed value if not found
	template <typename T>
	T find_first(condition cond, String item) {
		return find_first<T>(cbegin(), std::move(cond), item);
	}

	/// @brief Return the value for item @a item for the first row that matches condition @a cond
	/// when starting the search at @a pos
	/// @tparam The type of the value to return
	/// @param pos The location to start searching
	/// @param cond The condition to search for
	/// @param item The item for which the value should be returned
	/// @return The value found or a default constructed value if not found
	template <typename T>
	T find_first(const_iterator pos, condition cond, String item) {
		auto h = find<T>(pos, std::move(cond), item);

		return h.empty() ? T{} : *h.begin();
	}

	/// @brief Return a tuple containing the values for the items @a items for the first row that matches condition @a cond
	/// @tparam The types of the values to return
	/// @param cond The condition to search for
	/// @param items The items for which the values should be returned
	/// @return The values found or default constructed values if not found
	template <typename... Ts, typename... Cs, typename U = std::enable_if_t<sizeof...(Ts) != 1>>
	std::tuple<Ts...> find_first(condition &&cond, Cs... items) {
		static_assert(sizeof...(Ts) == sizeof...(Cs), "The number of item names should be equal to the number of types to return");
		// static_assert(std::is_same_v<Cs, const char*>..., "The item names should be const char");
		return find_first<Ts...>(cbegin(), std::move(cond), std::forward<Cs>(items)...);
	}

	/// @brief Return a tuple containing the values for the items @a items for the first row that matches condition @a cond
	/// when starting the search at @a pos
	/// @tparam The types of the values to return
	/// @param pos The location to start searching
	/// @param cond The condition to search for
	/// @param items The items for which the values should be returned
	/// @return The values found or default constructed values if not found
	template <typename... Ts, typename... Cs, typename U = std::enable_if_t<sizeof...(Ts) != 1>>
	std::tuple<Ts...> find_first(const_iterator pos, condition &&cond, Cs... items) {
		static_assert(sizeof...(Ts) == sizeof...(Cs), "The number of item names should be equal to the number of types to return");
		auto h = find<Ts...>(pos, std::move(cond), std::forward<Cs>(items)...);

		return h.empty() ? std::tuple<Ts...>{} : *h.begin();
	}

	// --------------------------------------------------------------------

	/// @brief Return the maximum value for item @a item for all rows that match condition @a cond
	/// @tparam The type of the value to return
	/// @param item The item to use for the value
	/// @param cond The condition to search for
	/// @return The value found or the minimal value for the type
	template <typename T, std::enable_if_t<std::is_arithmetic_v<T>, int> = 0>
	T find_max(String item, condition cond) {
		T result = std::numeric_limits<T>::min();

		for (auto v : find<T>(std::move(cond), item)) {
			if (result < v) {
				result = v;
			}
		}

		return result;
	}

	/// @brief Return the maximum value for item @a item for all rows
	/// @tparam The type of the value to return
	/// @param item The item to use for the value
	/// @return The value found or the minimal value for the type
	template <typename T, std::enable_if_t<std::is_arithmetic_v<T>, int> = 0>
	T find_max(String item) {
		return find_max<T>(item, all());
	}

	/// @brief Return the minimum value for item @a item for all rows that match condition @a cond
	/// @tparam The type of the value to return
	/// @param item The item to use for the value
	/// @param cond The condition to search for
	/// @return The value found or the maximum value for the type
	template <typename T, std::enable_if_t<std::is_arithmetic_v<T>, int> = 0>
	T find_min(String item, condition cond) {
		T result = std::numeric_limits<T>::max();

		for (auto v : find<T>(std::move(cond), item)) {
			if (result > v) {
				result = v;
			}
		}

		return result;
	}

	/// @brief Return the maximum value for item @a item for all rows
	/// @tparam The type of the value to return
	/// @param item The item to use for the value
	/// @return The value found or the maximum value for the type
	template <typename T, std::enable_if_t<std::is_arithmetic_v<T>, int> = 0>
	T find_min(String item) {
		return find_min<T>(item, all());
	}

	/// @brief Return whether a row exists that matches condition @a cond
	/// @param cond The condition to match
	/// @return True if a row exists
	@Deprecated//("Use contains instead")]] 
	public boolean exists(condition cond) {
		return contains(std::move(cond));
	}

	/// @brief Return whether a row exists that matches condition @a cond
	/// @param cond The condition to match
	/// @return True if a row exists
	public boolean contains(condition cond) {
		boolean result = false;

		if (cond) {
			cond.prepare(this);

			auto sh = cond.single();

			if (sh.has_value() && sh) {
				result = true;
			} else {
				for (auto r : this) {
					if (cond(r)) {
						result = true;
						break;
					}
				}
			}
		}

		return result;
	}

	/// @brief Return the total number of rows that match condition @a cond
	/// @param cond The condition to match
	/// @return The count
	public size_t count(condition cond) {
		size_t result = 0;

		if (cond) {
			cond.prepare(this);

			auto sh = cond.single();

			if (sh.has_value() && *sh) {
				result = 1;
			} else {
				for (auto r : this) {
					if (cond(r)) {
						++result;
					}
				}
			}
		}

		return result;
	}

	// --------------------------------------------------------------------

	/// Using the relations defined in the validator, return whether the row
	/// in @a r has any children in other categories
	public boolean has_children(row_handle r);

	/// Using the relations defined in the validator, return whether the row
	/// in @a r has any parents in other categories
	public boolean has_parents(row_handle r);

	/// Using the relations defined in the validator, return the row handles
	/// for all rows in @a childCat that are linked to row @a r
	public Vector<row_handle> get_children(row_handle r, final category childCat);

	/// Using the relations defined in the validator, return the row handles
	/// for all rows in @a parentCat that are linked to row @a r
	public Vector<row_handle> get_parents(row_handle r, final category parentCat);

	/// Using the relations defined in the validator, return the row handles
	/// for all rows in @a cat that are in any way linked to row @a r
	public Vector<row_handle> get_linked(row_handle r, final category cat);

	// --------------------------------------------------------------------

	// void insert(const_iterator pos, const row_initializer &row)
	// {
	// 	insert_impl(pos, row);
	// }

	// void insert(const_iterator pos, row_initializer &&row)
	// {
	// 	insert_impl(pos, std::move(row));
	// }

	/// Erase the row pointed to by @a pos and return the iterator to the
	/// row following pos.
	iterator erase(iterator pos);

	/// Erase row @a rh
	void erase(row_handle rh) {
		erase(iterator(this, rh.m_row));
	}

	/// @brief Erase all rows that match condition @a cond
	/// @param cond The condition
	/// @return The number of rows that have been erased
	size_t erase(condition cond);

	/// @brief Erase all rows that match condition @a cond calling
	/// the visitor function @a visit for each before actually erasing it.
	/// @param cond The condition
	/// @param visit The visitor function
	/// @return The number of rows that have been erased
	size_t erase(condition cond, std::function<void(row_handle)> visit);

	/// @brief Emplace the values in @a ri in a new row
	/// @param ri An object containing the values to insert
	/// @return iterator to the newly created row
	iterator emplace(row_initializer ri) {
		return this.emplace(ri.begin(), ri.end());
	}

	/// @brief Create a new row and emplace the values in the range @a b to @a e in it
	/// @param b Iterator to the beginning of the range of @ref item_value
	/// @param e Iterator to the end of the range of @ref item_value
	/// @return iterator to the newly created row
	template <typename ItemIter>
	iterator emplace(ItemIter b, ItemIter e) {
		row r = this.create_row();

		try {
			for (auto i = b; i != e; ++i) {
				// item_value *new_item = this->create_item(*i);
				r.append(add_item(i->name()), { i.value() });
			}
		} catch (Throwable t) {
			if (r != null) {
				this.delete_row(r);
			}
			throw t;
		}

		return insert_impl(cend(), r);
	}

	/// @brief Completely erase all rows contained in this category
	public void clear();

	// --------------------------------------------------------------------
	/// \brief generate a new, unique ID. Pass it an ID generating function
	/// based on a sequence number. This function will be called until the
	/// result is unique in the context of this category
	public String get_unique_id(std::function<String(int)> generator = cif::cif_id_for_number);

	/// @brief Generate a new, unique ID based on a string prefix followed by a number
	/// @param prefix The string prefix
	/// @return a new unique ID
	public String get_unique_id(final String prefix) {
		return get_unique_id([prefix](int nr)
			{ return prefix + std::to_string(nr + 1); });
	}

	/// @brief Generate a new, unique value for a item named @a item_name
	/// @param item_name The name of the item
	/// @return a new unique value
	public String get_unique_value(String item_name);

	// --------------------------------------------------------------------

	//using value_provider_type = std::function<String(String)>;

	/// \brief Update a single item named @a item_name in the rows that match
	/// \a cond to values provided by a callback function \a value_provider
	/// making sure the linked categories are updated according to the link.
	/// That means, child categories are updated if the links are absolute
	/// and unique. If they are not, the child category rows are split.

	void update_value(condition cond, String item_name, value_provider_type value_provider) {
		auto rs = find(std::move(cond));
		std::vector<row_handle> rows;
		std::copy(rs.begin(), rs.end(), std::back_inserter(rows));
		update_value(rows, item_name, std::move(value_provider));
	}

	/// \brief Update a single item named @a item_name in the rows \a rows
	/// to values provided by a callback function \a value_provider
	/// making sure the linked categories are updated according to the link.
	/// That means, child categories are updated if the links are absolute
	/// and unique. If they are not, the child category rows are split.

	public void update_value(Vector<row_handle> rows, String item_name,
		value_provider_type value_provider);

	/// \brief Update a single item named @a item_name in the rows that match \a cond to value \a value
	/// making sure the linked categories are updated according to the link.
	/// That means, child categories are updated if the links are absolute
	/// and unique. If they are not, the child category rows are split.

	public void update_value(condition cond, String item_name, String value) {
		auto rs = find(std::move(cond));
		Vector<row_handle> rows = new Vector<row_handle>();
		std::copy(rs.begin(), rs.end(), std::back_inserter(rows));
		update_value(rows, item_name, value);
	}

	/// \brief Update a single item named @a item_name in @a rows to value \a value
	/// making sure the linked categories are updated according to the link.
	/// That means, child categories are updated if the links are absolute
	/// and unique. If they are not, the child category rows are split.

	public void update_value(final Vector<row_handle> rows, String item_name, String value) {
		update_value(rows, item_name, [value](String) { return value; });
	}

	// --------------------------------------------------------------------
	// Naming used to be very inconsistent. For backward compatibility,
	// the old function names are here as deprecated variants.

	/// \brief Return the index number for \a column_name
	@Deprecated//("Use get_item_ix instead")]]
	public short get_column_ix(String column_name) {
		return get_item_ix(column_name);
	}

	/// @brief Return the name for column with index @a ix
	/// @param ix The index number
	/// @return The name of the column
	@Deprecated//("use get_item_name instead")]]
	public String get_column_name(short ix) {
		return get_item_name(ix);
	}

	/// @brief Make sure a item with name @a item_name is known and return its index number
	/// @param item_name The name of the item
	/// @return The index number of the item
	@Deprecated//("use add_item instead")]]
	public short add_column(String item_name) {
		return add_item(item_name);
	}

	/** @brief Remove column name @a colum_name
	 * @param column_name The column to be removed
	 */
	@Deprecated//("use remove_item instead")]]
	public void remove_column(String column_name) {
		remove_item(column_name);
	}

	/** @brief Rename column @a from_name to @a to_name */
	@Deprecated//("use rename_item instead")]]
	public void rename_column(String from_name, String to_name) {
		rename_item(from_name, to_name);
	}

	/// @brief Return whether a column with name @a name exists in this category
	/// @param name The name of the column
	/// @return True if the column exists
	@Deprecated//("use has_item instead")]]
	public boolean has_column(String name) {
		return has_item(name);
	}

	/// @brief Return the cif::iset of columns in this category
	@Deprecated//("use get_items instead")]]
	public iset get_columns() {
		return get_items();
	}

	// --------------------------------------------------------------------
	/// \brief Return the index number for \a item_name

	short get_item_ix(String item_name) {
		short result;

		for (result = 0; result < m_items.size(); ++result)
		{
			if (iequals(item_name, m_items[result].m_name))
				break;
		}

		if (VERBOSE > 0 && result == m_items.size() && m_cat_validator != null) // validate the name, if it is known at all (since it was not found)
		{
			auto iv = m_cat_validator.get_validator_for_item(item_name);
			if (iv == null) {
				System.err.println("Invalid name used '" + item_name + "' is not a known item in " + m_name + '\n');
			}
		}

		return result;
	}

	/// @brief Return the name for item with index @a ix
	/// @param ix The index number
	/// @return The name of the item
	public String get_item_name(short ix) {
		if (ix >= m_items.size()) {
			throw new ValueOutOfRangeException("item index is out of range");
		}

		return m_items[ix].m_name;
	}

	/// @brief Make sure a item with name @a item_name is known and return its index number
	/// @param item_name The name of the item
	/// @return The index number of the item
	public short add_item(String item_name) {
		//using namespace std::literals;

		short result = get_item_ix(item_name);

		if (result == m_items.size()) {
			item_validator item_validator = null;

			if (m_cat_validator != null) {
				item_validator = m_cat_validator.get_validator_for_item(item_name);
				if (item_validator == null)
					m_validator.report_error( validation_error.item_not_allowed_in_category, m_name, item_name, false);
			}

			m_items.emplace_back(item_name, item_validator);
		}

		return result;
	}

	/** @brief Remove item name @a colum_name
	 * @param item_name The item to be removed
	 */
	public void remove_item(String item_name);

	/** @brief Rename item @a from_name to @a to_name */
	public void rename_item(String from_name, String to_name);

	/// @brief Return whether a item with name @a name exists in this category
	/// @param name The name of the item
	/// @return True if the item exists
	public boolean has_item(String name) {
		return get_item_ix(name) < m_items.size();
	}

	/// @brief Return the cif::iset of items in this category
	public iset get_items();

	// --------------------------------------------------------------------

	/// @brief Sort the rows using comparator function @a f
	/// @param f The comparator function taking two row_handles and returning
	/// an int indicating whether the first is smaller, equal or larger than
	/// the second. ( respectively a value <0, 0, or >0 )
	public void sort(std::function<int(row_handle, row_handle)> f);

	/// @brief Reorder the rows in the category using the index defined by
	/// the @ref category_validator
	public void reorder_by_index();

	// --------------------------------------------------------------------

	/// This function returns effectively the list of fully qualified item
	/// names, that is category_name + '.' + item_name for each item
	@Deprecated//("use get_item_order instead")]]
	public Vector<String> get_tag_order() {
		return get_item_order();
	}

	/// This function returns effectively the list of fully qualified item
	/// names, that is category_name + '.' + item_name for each item
	public Vector<String> get_item_order();

	/// Write the contents of the category to the std::ostream @a os
	public void write(OStream os);

	/// @brief Write the contents of the category to the std::ostream @a os and
	/// use @a order as the order of the items. If @a addMissingItems is
	/// false, items that do not contain any value will be suppressed
	/// @param os The std::ostream to write to
	/// @param order The order in which the items should appear
	/// @param addMissingItems When false, empty items are suppressed from the output
	public void write(OStream os, final Vector<String> order) {
		write(os, order, true);
	}
	
	
	public void write(OStream os, final Vector<String> order, boolean addMissingItems);


	private void write(OStream os, final Vector<Short> order, boolean includeEmptyItems);

	//public:
	/// friend function to make it possible to do:
	/// @code {.cpp}
	/// std::cout << my_category;
	/// @endcode
	public OStream operator_ltlt(OStream os, category &cat) {
		cat.write(os);
		return os;
	}

  
	private void update_value(row row, short item, String value, bool updateLinked, bool validate = true);

	private void erase_orphans(condition cond, category parent);

	//using allocator_type = std::allocator<void>;

	private constexpr allocator_type get_allocator() {
		return {};
	}

	/*using char_allocator_type = typename std::allocator_traits<allocator_type>::template rebind_alloc<char>;
	using char_allocator_traits = std::allocator_traits<char_allocator_type>;

	using row_allocator_type = typename std::allocator_traits<allocator_type>::template rebind_alloc<row>;
	using row_allocator_traits = std::allocator_traits<row_allocator_type>;*/

	private row_allocator_traits::pointer get_row() {
		row_allocator_type ra(get_allocator());
		return row_allocator_traits::allocate(ra, 1);
	}

	private row create_row() {
		auto p = this.get_row();
		row_allocator_type ra(get_allocator());
		row_allocator_traits::construct(ra, p);
		return p;
	}

	private row clone_row(final row r);

	private void delete_row(row r);

	private row_handle create_copy(row_handle r);

	// proxy methods for every insertion
	private iterator insert_impl(const_iterator pos, row n);
	private iterator erase_impl(const_iterator pos);

	// --------------------------------------------------------------------

	private condition get_parents_condition(row_handle rh, final category parentCat);
	private condition get_children_condition(row_handle rh, final category childCat);

	// --------------------------------------------------------------------

	private void swap_item(short item_ix, row_handle a, row_handle b);

	// --------------------------------------------------------------------	
}
