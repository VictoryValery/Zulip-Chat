package com.victoryvalery.tfsproject.presentation.screenProfile

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.terrakok.cicerone.Router
import com.victoryvalery.tfsproject.App
import com.victoryvalery.tfsproject.R
import com.victoryvalery.tfsproject.databinding.FragmentProfileBinding
import com.victoryvalery.tfsproject.di.profileComponent.DaggerProfileDependentComponent
import com.victoryvalery.tfsproject.domain.models.Status
import com.victoryvalery.tfsproject.domain.models.UserItem
import com.victoryvalery.tfsproject.presentation.screenMainActivity.MainActivity
import com.victoryvalery.tfsproject.presentation.screenProfile.Event.Ui
import vivid.money.elmslie.android.base.ElmFragment
import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.storepersisting.retainStoreHolder
import javax.inject.Inject

class ProfileFragment : ElmFragment<Event, Effect, State>() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var storeFactory: StoreFactory

    @Inject
    lateinit var router: Router

    private var user: UserItem? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val appComponent = App.INSTANCE.appComponent
        val profileComponent = DaggerProfileDependentComponent.builder().appComponent(appComponent).build()
        profileComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arguments?.getParcelable(USER, UserItem::class.java)
        else
            arguments?.getParcelable(USER)

        if (savedInstanceState == null)
            if (user != null) {
                store.accept(Ui.SetUser(user!!))
                (requireActivity() as MainActivity).setNavigationViewVisibility(false)
            } else {
                binding.profileToolbar.isVisible = false
                binding.profileShimmer.profileToolbar.isVisible = false
                store.accept(Ui.LoadUser)
            }

        binding.backArrowToolbar.setOnClickListener {
            router.exit()
        }
    }

    override val initEvent: Event = Ui.Initial

    private val retainStore = retainStoreHolder {
        storeFactory.provide()
    }

    override val storeHolder: StoreHolder<Event, Effect, State>
        get() = retainStore.value

    override fun render(state: State) {
        with(binding) {
            profileShimmer.root.isVisible = state.isLoading
            if (state.userInfo != null)
                fillUser(state.userInfo.user, state.userInfo.status)

            if (state.isError)
                profileShimmer.root.isVisible = false
        }
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.ShowError -> {
                if (effect.errorMessage.contains(getString(R.string.Internet_error), true))
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.check_internet_connection), Toast.LENGTH_SHORT
                    ).show()
                else
                    Toast.makeText(requireContext(), effect.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fillUser(user: UserItem, status: Status) {
        binding.apply {
            profileName.text = user.fullName
            profileWebstatusText.apply {
                text = when (status) {
                    is Status.Active -> {
                        setTextColor(ContextCompat.getColor(context, R.color.profile_active))
                        context.getString(R.string.status_active)
                    }
                    is Status.Idle -> {
                        setTextColor(ContextCompat.getColor(context, R.color.profile_idle))
                        context.getString(R.string.status_idle)
                    }
                    else -> {
                        setTextColor(ContextCompat.getColor(context, R.color.profile_offline))
                        context.getString(R.string.status_offline)
                    }
                }
                Glide.with(root)
                    .load(user.avatarUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.common_placeholder)
                    .into(profileAvatarImage)

                if (!user.isMeUser) {
                    (requireActivity() as MainActivity).setNavigationViewVisibility(false)
                } else {
                    binding.profileToolbar.isVisible = false
                    binding.profileShimmer.profileToolbar.isVisible = false
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (requireActivity() as MainActivity).setNavigationViewVisibility(true)
    }

    companion object {
        private const val USER = "USER"
        fun getNewInstance(user: UserItem): ProfileFragment {
            return ProfileFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(USER, user)
                }
            }
        }
    }
}

