package com.x7th.sole;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "accounts")
public class AccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long id;

	@Column(nullable = false)
	public String username;

	@Column(nullable = false)
	public String password;

	@Column(nullable = false)
	public String description;

	@Column(nullable = false)
	public boolean valid;
}
