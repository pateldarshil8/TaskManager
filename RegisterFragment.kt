package org.taskflow.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.taskflow.app.MainActivity
import org.taskflow.app.R
import org.taskflow.app.data.model.User
import org.taskflow.app.databinding.FragmentRegisterBinding
import org.taskflow.app.util.*
import org.taskflow.app.util.UiState.*

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private var _uiBinding: FragmentRegisterBinding? = null
    private val uiBinding get() = _uiBinding!!
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _uiBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        return uiBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        uiBinding.register.setOnClickListener {
            if (isInputValid()) {
                authViewModel.register(
                    email = uiBinding.email.text.toString(),
                    password = uiBinding.password.text.toString(),
                    user = buildUser()
                )
            }
        }
    }

    private fun setupObserver() {
        authViewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Loading -> uiBinding.loading.show()
                is Failure -> {
                    uiBinding.loading.hide()
                    snackbar("Registration failed. Please try again.")
                }
                is Success -> {
                    uiBinding.loading.hide()
                    snackbar(state.data)
                    (activity as? AppCompatActivity)?.supportActionBar?.hide()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                }
            }
        }
    }

    private fun isInputValid(): Boolean {
        var isValid = true

        if (uiBinding.name.text.isNullOrBlank()) {
            snackbar(getString(R.string.enter_name))
            isValid = false
        }

        val emailText = uiBinding.email.text.toString()
        if (emailText.isBlank()) {
            snackbar(getString(R.string.enter_email))
            isValid = false
        } else if (!emailText.isValidEmail()) {
            snackbar(getString(R.string.invalid_email))
            isValid = false
        }

        val passwordText = uiBinding.password.text.toString()
        if (passwordText.isBlank()) {
            snackbar(getString(R.string.enter_password))
            isValid = false
        } else if (passwordText.length < 8) {
            snackbar(getString(R.string.invalid_password))
            isValid = false
        }

        return isValid
    }

    private fun buildUser(): User {
        return User(
            id = "",
            name = uiBinding.name.text.toString(),
            email = uiBinding.email.text.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _uiBinding = null
    }
}
