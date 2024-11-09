/*
 *     Julia language support plugin for Intellij-based IDEs.
 *     Copyright (C) 2024 julia-intellij contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.ice1000.julia.lang.error;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

@VisibleForTesting
public final class YoutrackApi {
	// https://camelcade.myjetbrains.com/youtrack/api/admin/projects
	private static final String PROJECT_ID = "81-1";
	private static final YoutrackProject PROJECT = new YoutrackProject(PROJECT_ID);

	// https://camelcade.myjetbrains.com/youtrack/api/admin/projects/81-1/customFields?fields=id,canBeEmpty,emptyFieldText,project(id,name),field(id,name,fieldType(id))
	private static final YoutrackSingleCustomField PRIORITY =
		new YoutrackSingleCustomField("Priority", "SingleEnumIssueCustomField", "Normal");
	private static final YoutrackSingleCustomField TYPE = new YoutrackSingleCustomField("Type", "SingleEnumIssueCustomField", "Exception");

	private YoutrackApi() {
	}

	static final class YoutrackCustomFieldValue {
		@SuppressWarnings("unused")
		@Expose
		public final String name;

		public YoutrackCustomFieldValue(String name) {
			this.name = name;
		}
	}

	private abstract static class YoutrackCustomField {
		@SuppressWarnings("unused")
		@Expose
		public final String name;
		@SuppressWarnings("unused")
		@Expose
		@SerializedName("$type")
		public final String type;

		public YoutrackCustomField(String name, String type) {
			this.name = name;
			this.type = type;
		}
	}

	static final class YoutrackSingleCustomField extends YoutrackCustomField {
		@SuppressWarnings("unused")
		@Expose
		public final YoutrackCustomFieldValue value;

		public YoutrackSingleCustomField(String name, String type, String value) {
			super(name, type);
			this.value = new YoutrackCustomFieldValue(value);
		}
	}


	static final class YoutrackProject {
		@SuppressWarnings("unused")
		@Expose
		public final String id;

		public YoutrackProject(String id) {
			this.id = id;
		}
	}

	@VisibleForTesting
	public static final class YoutrackIssue {
		@Expose
		public final YoutrackProject project = PROJECT;
		@Expose
		public final List<? super YoutrackCustomField> customFields = new ArrayList<>();
		@Expose
		public String summary;
		@Expose
		public String description;

		public YoutrackIssue(String summary, String description) {
			this.summary = summary;
			this.description = description;
			customFields.add(PRIORITY);
			customFields.add(TYPE);
		}

		@Override
		public String toString() {
			return "YoutrackIssue{" +
				"project=" + project +
				", customFields=" + customFields +
				", summary='" + summary + '\'' +
				", description='" + description + '\'' +
				'}';
		}
	}

	@VisibleForTesting
	public static final class YoutrackIssueResponse {
		@Expose
		public String id;
		@Expose
		public String idReadable;
		@Expose
		@SerializedName("$type")
		public String type;

		public YoutrackIssue issue;
		public int attachmentsAdded = 0;

		@Override
		public String toString() {
			return "YoutrackIssueResponse{" +
				"id='" + id + '\'' +
				", idReadable='" + idReadable + '\'' +
				", type='" + type + '\'' +
				", issue=" + issue +
				'}';
		}
	}
}
